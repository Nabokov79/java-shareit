package ru.practicum.shareit.requests.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserService userService,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestCreateDto);
        itemRequest.setRequester(UserMapper.toUser(userService.getUser(userId)));
        logger.info("Create request user = " + userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        userService.getUser(userId);
        List<ItemRequest> itemRequestDb = itemRequestRepository.findAllByRequesterId(userId);
        List<ItemRequestResponseDto> itemRequestList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestDb) {
            ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
            itemRequestResponseDto.setItems(itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                                                          .map(ItemMapper::toItemDto).collect(Collectors.toList()));
            itemRequestList.add(itemRequestResponseDto);
        }
        return itemRequestList;
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequest(int from, int size, Long userId) {
        if (size == 0) {
            throw new BadRequestException(String.format("Size cannot be  %s", size));
        }
        userService.getUser(userId);
        Pageable pageable = PageRequest.of(from,size, Sort.by("owner_id").descending());
        List<ItemRequest> itemRequestDb = itemRequestRepository.findAll().stream()
                                                    .filter(itemRequest -> itemRequest.getRequester().getId() != userId)
                                                    .collect(Collectors.toList());
        List<ItemRequestResponseDto> itemRequestList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestDb) {
            ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
            itemRequestResponseDto.setItems(itemRepository.findAllByOwnerId(userId, pageable).stream()
                                                   .filter(item -> item.getRequest() != null).map(ItemMapper::toItemDto)
                                                   .collect(Collectors.toList()));
            itemRequestList.add(itemRequestResponseDto);
        }
        return itemRequestList;
    }

    @Override
    public ItemRequestResponseDto getRequest(Long requestId, Long userId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                            .orElseThrow(() -> new NotFoundException(String.format("request %s not found", requestId)));
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
        itemRequestResponseDto.setItems(itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                                                      .map(ItemMapper::toItemDto).collect(Collectors.toList()));
        return itemRequestResponseDto;
    }
}