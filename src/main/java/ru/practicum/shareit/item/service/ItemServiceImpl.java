package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ItemServiceImpl(ItemRepository repository,
                           CommentRepository commentRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = repository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
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
                .orElseThrow(() -> new NotFoundException(String.format("Item %s not found",itemId))));
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found", userId))));
        commentRepository.save(comment);
        logger.info("Create comment");
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
        return ItemMapper.toItemDto(itemRepository.save(itemDb));
    }

    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        Item itemDb = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException(String.format("Item %s not found", itemId)));
        logger.info("Get item " + itemId);
        return setResponseParametersItem(itemDb , userId , itemId);
    }

    @Override
    public List<ItemResponseDto> getAllItems(Long userId) {
        List<Item> itemsDb =  itemRepository.findAllByOwnerId(userId);
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
        itemRepository.deleteById(itemId);
        logger.info("Delete item " + itemId);
    }

    @Override
    public List<ItemDto> searchItemByNameAndDesctription(Long userId, String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (text.equals("")) {
            return itemDtoList;
        }
        for (Item item : itemRepository.findAll()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                if (userId != 0) {
                    itemDtoList.add(ItemMapper.toItemDto(item));
                } else {
                    itemDtoList.add(ItemMapper.toItemDto(item));
                }
            }
        }
        logger.info("Search by name and desctription  for user = " + userId);
        return itemDtoList;
    }

    private void setCreateParametersItem(ItemDto itemDto, Item item, long userId) {
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
        item.setRequest(null);
        logger.info("Set parameters for item users = " + userId);
    }

    private ItemResponseDto setResponseParametersItem(Item itemDb, Long userId, Long itemId) {
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(itemDb);
        List<Booking> bookingItem = bookingRepository.findBookingByItemId(itemId).stream()
                                                     .filter(booking -> booking.getStatus()
                                                     .toString().equals("APPROVED"))
                                                     .collect(Collectors.toList());
        if (bookingItem.isEmpty()) {
            itemResponseDto.setLastBooking(ItemMapper.toItemBooking(new Booking()));
            itemResponseDto.setNextBooking(ItemMapper.toItemBooking(new Booking()));
        } else {
            if (userId == itemDb.getOwner().getId()) {
                ItemBooking lastBooking = bookingItem.stream()
                                                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                                                    .map(ItemMapper::toItemBooking).collect(Collectors.toList()).get(0);
                ItemBooking nextBooking = bookingItem.stream()
                                                    .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                                                    .map(ItemMapper::toItemBooking).collect(Collectors.toList()).get(0);
                itemResponseDto.setLastBooking(lastBooking);
                itemResponseDto.setNextBooking(nextBooking);
            } else {
                itemResponseDto.setLastBooking(ItemMapper.toItemBooking(new Booking()));
                itemResponseDto.setNextBooking(ItemMapper.toItemBooking(new Booking()));
            }
        }
        List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId).stream()
                                                             .map(CommentMapper::toCommentResponseDto)
                                                             .collect(Collectors.toList());
        itemResponseDto.setComments(comments);
        logger.info("Get item bookings by item_id = " + itemId + " user = " + userId);
        return itemResponseDto;
    }
}