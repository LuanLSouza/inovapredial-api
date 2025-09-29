package com.inovapredial.service;

import com.inovapredial.dto.BuildingFilterDTO;
import com.inovapredial.dto.requests.BuildingRequestDTO;
import com.inovapredial.dto.responses.BuildingResponseDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.BuildingMapper;
import com.inovapredial.model.Address;
import com.inovapredial.model.Building;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.AddressRepository;
import com.inovapredial.repository.BuildingRepository;
import com.inovapredial.specification.BuildingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingMapper mapper;
    private final AddressRepository addressRepository;
    private final BuildingRepository buildingRepository;
    private final SecurityContextService securityContextService;

    @Transactional
    public Building create(BuildingRequestDTO dto) {

        OwnUser currentUser = securityContextService.getCurrentUser();

        var address = mapper.toEntity(dto.addressRequest());
        var toSave = mapper.toEntity(dto);

        toSave.setAddress(address);

        Building savedBuilding = buildingRepository.save(toSave);
        savedBuilding.getUsers().add(currentUser);

        currentUser.getBuildings().add(savedBuilding);

        return  buildingRepository.save(savedBuilding);
    }

    @Transactional
    public Building update(String id, BuildingRequestDTO dto) {

        var buildingToUpdate = findById(id);

        mapper.updateBuildingFromRequestDTO(dto, buildingToUpdate);

        if (dto.addressRequest() != null) {
            Address addressToUpdate = buildingToUpdate.getAddress();

            if (addressToUpdate == null) {
                addressToUpdate = mapper.toEntity(dto.addressRequest());
                addressToUpdate = addressRepository.save(addressToUpdate);
                buildingToUpdate.setAddress(addressToUpdate);
            } else {
                mapper.updateAddressFromRequestDTO(dto.addressRequest(), addressToUpdate);
                addressRepository.save(addressToUpdate);
            }
        }
        return buildingRepository.save(buildingToUpdate);

    }

    public Building findById(String id) {
        return buildingRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Building not found"));
    }

    public  void delete(String id) {
        var building = findById(id);
        buildingRepository.delete(building);
    }

    public PageResponseDTO<BuildingResponseDTO> findAllWithFilters(BuildingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        var ownUser = securityContextService.getCurrentUser();
        Page<Building> buildingPage = buildingRepository.findAll(BuildingSpecification.withFilters(filter, ownUser), pageable);
        
        List<BuildingResponseDTO> content = buildingPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return PageResponseDTO.<BuildingResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(buildingPage.getTotalElements())
                .totalPages(buildingPage.getTotalPages())
                .hasNext(buildingPage.hasNext())
                .hasPrevious(buildingPage.hasPrevious())
                .build();
    }
}
