package ru.antoncharov.socks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.antoncharov.socks.domain.Socks;
import ru.antoncharov.socks.dto.SocksDto;
import ru.antoncharov.socks.exception.NotEnoughSocksException;
import ru.antoncharov.socks.service.SocksService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Socks Management", description = "Operations for managing socks inventory")
@RequiredArgsConstructor
public class SocksController {

    private static final Logger logger = LoggerFactory.getLogger(SocksController.class);

    private final SocksService socksService;

    @Operation(
            summary = "Register incoming socks",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Socks successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PostMapping("/income")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerIncomingSocks(@Valid @RequestBody SocksDto sockDto) {
        socksService.addSocks(sockDto);
        logger.info("Registered incoming socks with color {}, cotton percentage {} and quantity {}", sockDto.color(), sockDto.cottonPercentage(), sockDto.quantity());
    }

    @Operation(
            summary = "Register outgoing socks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Socks successfully removed"),
                    @ApiResponse(responseCode = "404", description = "Not enough socks or no matching socks found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PostMapping("/outcome")
    @ResponseStatus(HttpStatus.OK)
    public void registerOutgoingSocks(@Valid @RequestBody SocksDto sockDto) throws NotEnoughSocksException {
        socksService.removeSocks(sockDto);
        logger.info("Registered outgoing socks with color {}, cotton percentage {} and quantity {}", sockDto.color(), sockDto.cottonPercentage(), sockDto.quantity());
    }

    @Operation(
            summary = "Get total number of socks by criteria",
            parameters = {
                    @Parameter(name = "color", description = "Color of the socks"),
                    @Parameter(name = "comparisonOperator", description = "Comparison operator for cotton percentage"),
                    @Parameter(name = "cottonPercentage", description = "Cotton percentage to compare against")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total number of socks retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @GetMapping
    public List<Socks> getSocks(@RequestParam(required = false) String color,
                                @RequestParam(required = false) Integer cottonPercentage,
                                @RequestParam(defaultValue = "equal") String comparisonOperator) {
        return socksService.getSocks(color, cottonPercentage, comparisonOperator);
    }

    @Operation(
            summary = "Update socks details",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Socks updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Sock not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSock(@PathVariable Long id, @Valid @RequestBody SocksDto sockDto) {
        socksService.updateSock(id, sockDto);
        logger.info("Updated socks with ID {}", id);
    }

    @Operation(
            summary = "Register incoming socks",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Socks successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchSocks(@RequestParam("file") MultipartFile file) throws IOException {
        socksService.batchFromFile(file);
    }
}
