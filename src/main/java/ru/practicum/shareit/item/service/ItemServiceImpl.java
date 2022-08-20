package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        userService.getUser(userId);
        itemDto.setOwner(userService.getUser(userId).getId());
        return ItemMapper.toItemDto(repository.addItem(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        return ItemMapper.toItemDto(repository.updateItem(userId, itemId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto getItem(long itemId) {
        return ItemMapper.toItemDto(repository.getItem(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : repository.getAllItems()) {
            if (userId != 0) {
                if (item.getOwner() == userId) {
                    itemDtoList.add(getItem(item.getId()));
                }

            } else {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
        }
        return itemDtoList;
    }

    @Override
    public void deleteItem(long itemId) {
        repository.deleteItem(itemId);
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (text.equals("")){
            return itemDtoList;
        }
        for (Item item : repository.getAllItems()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                if (userId != 0) {
                    itemDtoList.add(ItemMapper.toItemDto(item));
                } else {
                    itemDtoList.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return itemDtoList;
    }
}