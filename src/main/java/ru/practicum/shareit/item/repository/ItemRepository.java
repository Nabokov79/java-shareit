package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.CustomBadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long id;

    public void generateId() {
        if (id == 0) {
            id = 1;
        }
        for (Long key :items.keySet()) {
            id = Math.max(id,key) + 1;
        }
    }

    public Item addItem(Item item) {
        generateId();
        item.setId(id);
        items.put(id, item);
        return getItem(item.getId());
    }

    public Item updateItem(long userId,long itemId, Item item) {
        item.setId(itemId);
        setItemParameters(itemId, item);
        if (userId != items.get(itemId).getOwner()) {
            throw new NotFoundException("Item for update not found");
        }
        if (userId == 0) {
            throw new CustomBadRequestException("Parameter useId not found");
        }
        items.put(itemId, item);
        return getItem(itemId);
    }

    public Item getItem(long itemId) {
        return items.get(itemId);
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public void deleteItem(long itemId) {
        items.remove(itemId);
    }
    private void setItemParameters(long itemId, Item item) {

        if (item.getName() == null) {
            item.setName(items.get(itemId).getName());
        }

        if (item.getDescription() == null) {
            item.setDescription(items.get(itemId).getDescription());
        }

        if (item.getOwner() == 0) {
            item.setOwner(items.get(itemId).getOwner());
        }

        if (item.getAvailable() == null) {
            item.setAvailable(items.get(itemId).getAvailable());
        }
    }
}