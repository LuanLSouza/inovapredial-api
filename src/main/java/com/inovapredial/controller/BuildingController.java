package com.inovapredial.controller;

import com.inovapredial.dto.BuildingRequestDTO;
import com.inovapredial.dto.BuildingResponseDTO;
import com.inovapredial.mapper.BuildingMapper;
import com.inovapredial.model.Building;
import com.inovapredial.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingMapper buildingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingResponseDTO create(@Valid @RequestBody BuildingRequestDTO dto){
        return  buildingMapper.toResponseDTO(buildingService.create(dto));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BuildingResponseDTO update(@PathVariable String id,
                                      @Valid @RequestBody BuildingRequestDTO dto) {

        Building updated = buildingService.update(id, dto);
        return buildingMapper.toResponseDTO(updated);

    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BuildingResponseDTO findById(@PathVariable String id) {
        Building building = buildingService.findById(id);
        return buildingMapper.toResponseDTO(building);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        buildingService.delete(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BuildingResponseDTO> findAll() {
        return buildingService.findAll();
    }

}
