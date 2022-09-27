package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.ItemBooking;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDTOJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private JacksonTester<ItemResponseDto> json2;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L,"item_dto", "item dto test", true, 1L, 1L);
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item_dto");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item dto test");
        assertThat(result).doesNotHaveJsonPath("$.true");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemResponseDto() throws Exception {
        ItemBooking lastBooking = new ItemBooking(2L, 1L);
        ItemBooking nextBooking = new ItemBooking(3L, 1L);
        CommentResponseDto comment = new CommentResponseDto(4L, "CommentResponseDto",
                "Comment", LocalDateTime.of(2022,9,27,10,10));
        ItemResponseDto itemDto = new ItemResponseDto(1L,"item_dto", "item dto test", true,
                                                                            lastBooking, nextBooking, List.of(comment));
        JsonContent<ItemResponseDto> result = json2.write(itemDto);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item_dto");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item dto test");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                                               .isEqualTo("CommentResponseDto");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                                                                .isEqualTo("Comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                                                 .isEqualTo("2022-09-27T10:10:00");
    }
}