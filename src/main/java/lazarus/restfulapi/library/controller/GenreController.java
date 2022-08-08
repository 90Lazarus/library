package lazarus.restfulapi.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lazarus.restfulapi.library.exception.ResourceNotFoundException;
import lazarus.restfulapi.library.exception.UniqueViolationException;
import lazarus.restfulapi.library.model.dto.GenreDTO;
import lazarus.restfulapi.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GenreController {
    @Autowired private GenreService genreService;

    @GetMapping("/genres")
    @Operation(summary = "Get the list of all available genres, optionally sorted by parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found genres in the database"),
            @ApiResponse(responseCode = "404", description = "No genres found in the database")
    })
    public List<GenreDTO> getAllGenres(@RequestParam(required = false, defaultValue = "0") Integer page,
                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                       @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction,
                                       @RequestParam(required = false, defaultValue = "id") String sortBy) {
        return genreService.readGenres(page, size, direction, sortBy);
    }

    @GetMapping("/genres/{id}")
    @Operation(summary = "View a genre with an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the genre")
    })
    public GenreDTO getGenreById(@PathVariable Long id) throws ResourceNotFoundException {
        return genreService.readGenreById(id);
    }

    @PostMapping("/genres")
    @Operation(summary = "Create a new unique genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New genre created")
    })
    public GenreDTO saveGenre(@RequestBody @Valid GenreDTO genreDTO) throws UniqueViolationException {
        return genreService.createGenre(genreDTO);
    }

    @PutMapping("/genres/{id}")
    @Operation(summary = "Modify a genre with an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre updated")
    })
    public GenreDTO updateGenre(@PathVariable Long id, @RequestBody @Valid GenreDTO genreDTO) throws ResourceNotFoundException, UniqueViolationException {
        return genreService.updateGenre(id, genreDTO);
    }

    @DeleteMapping("/genres/{id}")
    @Operation(summary = "Delete a genre with the an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre deleted")
    })
    public void deleteGenre(@PathVariable Long id) throws ResourceNotFoundException {
        genreService.deleteGenre(id);
    }
}
