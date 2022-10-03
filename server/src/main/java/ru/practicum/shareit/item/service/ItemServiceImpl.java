package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemBooking;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           CommentRepository commentRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        setCreateParametersItem(itemDto, item, userId);
        logger.info("Create item " + item.getId());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentDto, Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerId(itemId, userId).stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().toString().equals("APPROVED"))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new BadRequestException("User " + userId + " did not book item " + itemId);
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item %s not found", itemId))));
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found", userId))));
        commentRepository.save(comment);
        logger.info("Create comment for item = " + itemId);
        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item itemDb = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("item %s not found", itemId)));
        if (userId != itemDb.getOwner().getId()) {
            throw new NotFoundException(String.format("User %s not owner item", itemId));
        }
        setCreateParametersItem(itemDto, itemDb, userId);
        logger.info("Update item " + itemId);
        itemRepository.save(itemDb);
        return ItemMapper.toItemDto(itemDb);
    }

    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        Item itemDb = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item %s not found", itemId)));
        logger.info("Get item " + itemId);
        return setResponseParametersItem(itemDb, userId, itemId);
    }

    @Override
    public List<ItemResponseDto> getAllItems(int from, int size, Long userId) {
        Pageable pageable = PageRequest.of(from,size, Sort.by("Owner").descending());
        List<Item> itemsDb = itemRepository.findAllByOwnerId(userId, pageable)
                                       .stream().filter(item -> item.getRequest() == null).collect(Collectors.toList());
        List<ItemResponseDto> itemResponseDto = new ArrayList<>();
        if (itemsDb.isEmpty()) {
            throw new NotFoundException(String.format("Owner %s items not found", userId));
        }
        for (Item item : itemsDb) {
            itemResponseDto.add(setResponseParametersItem(item, userId, item.getId()));
        }
        logger.info("Get list items for user " + userId);
        return itemResponseDto;
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.findById(itemId)
                      .orElseThrow(() -> new NotFoundException(String.format("Item not found %s", itemId)));
        logger.info("Delete item " + itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItemByNameAndDesctription(Long userId, String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (text.equals("")) {
            return itemDtoList;
        }
        List<Item> itemDbList = itemRepository.findAll();
        if (itemDbList.isEmpty()) {
            logger.info("itemDbList empty for user = " + userId);
            throw new BadRequestException("Items not found.");
        }
        for (Item item : itemDbList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
        }
        logger.info("Search by name and desctription for user = " + userId);
        return itemDtoList;
    }

    private void setCreateParametersItem(ItemDto itemDto, Item item, Long userId) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user %s not found", userId))));
        if (itemDto.getRequestId() == null) {
            item.setRequest(null);
        } else {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId())
                              .orElseThrow(() -> new NotFoundException(String.format("Request %s not found", userId))));
        }

        logger.info("Set parameters for item user id = " + userId);
    }

    private ItemResponseDto setResponseParametersItem(Item itemDb, Long userId, Long itemId) {
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(itemDb);
        List<Booking> bookingItem = bookingRepository.findBookingByItemId(itemId).stream()
                                                                                 .filter(booking -> booking.getStatus()
                                                                                 .toString().equals("APPROVED"))
                                                                                 .collect(Collectors.toList());
        if (!bookingItem.isEmpty() && userId == itemDb.getOwner().getId()) {
            List<ItemBooking> lastBooking = bookingItem.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .map(ItemMapper::toItemBooking).collect(Collectors.toList());
            List<ItemBooking> nextBooking = bookingItem.stream()
                    .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                    .map(ItemMapper::toItemBooking).collect(Collectors.toList());
            if (!lastBooking.isEmpty()) {
                itemResponseDto.setLastBooking(lastBooking.get(0));
            }
            if (!nextBooking.isEmpty()) {
                itemResponseDto.setNextBooking(nextBooking.get(0));
            }
            logger.info("Get item bookings by item_id = " + itemId + " user = " + userId);
        }
        List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
        itemResponseDto.setComments(comments);

        return itemResponseDto;
    }
}
