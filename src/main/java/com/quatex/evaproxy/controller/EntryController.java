package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.dto.CommentDto;
import com.quatex.evaproxy.dto.EntryDataDto;
import com.quatex.evaproxy.service.ManageService;
import com.quatex.evaproxy.service.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class EntryController {

    private final PromoCodeService promoCodeService;
    private final ManageService manageService;
    public EntryController(PromoCodeService promoCodeService, ManageService manageService) {
        this.promoCodeService = promoCodeService;
        this.manageService = manageService;
    }

    @Operation(summary = "Get entry data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EntryDataDto.class)),
                    })
    })
    @GetMapping("/entryData")
    public Mono<EntryDataDto> getEntryData(@RequestParam(defaultValue = "1") Integer version) {
        return Mono.zip(
                promoCodeService.getAll().collectList(),
                manageService.getSettings(version)
        ).map(data -> new EntryDataDto(
                data.getT1(),
                data.getT2().getLink(),
                data.getT2().getLinkCryptoPay(),
                data.getT2().getEnabled()
        ));
    }


    //#########################mvp################################
    @Operation(summary = "Get random numbers")
    @GetMapping("/numbers")
    public Mono<List<Integer>> getRandomNumbers() {
        List<Integer> result = new Random().ints(64, 2, 6)
                .boxed()
                .collect(Collectors.toList());
        result.set(0, 1);
        result.set(9, 1);
        result.set(18, 1);
        result.set(27, 1);
        result.set(36, 1);
        result.set(45, 1);
        result.set(54, 1);
        result.set(63, 1);
        return Mono.just(result);
    }

    List<CommentDto> comments = new ArrayList<>();
    @Operation(summary = "Clear all comments")
    @DeleteMapping("/post/comment/clear")
    public Mono<List<CommentDto>> clear() {
        comments.clear();
        return Mono.just(comments);
    }

    @Operation(summary = "Get comments")
    @GetMapping("/post/comment/list")
    public Mono<List<CommentDto>> getComments() {
        return Mono.just(comments);
    }

    @Operation(summary = "Add new comment")
    @PostMapping("/post/comment")
    public Mono<CommentDto> addComment(@RequestBody CommentDto commentDto) {
        commentDto.setDate(LocalDateTime.now(ZoneOffset.UTC));
        comments.add(commentDto);
        return Mono.just(commentDto);
    }
}
