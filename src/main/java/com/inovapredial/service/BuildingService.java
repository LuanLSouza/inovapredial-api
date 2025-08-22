package com.inovapredial.service;

import com.inovapredial.dto.BuildingRequestDTO;
import com.inovapredial.dto.BuildingResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.BuildingMapper;
import com.inovapredial.model.Address;
import com.inovapredial.model.Building;
import com.inovapredial.repository.AddressRepository;
import com.inovapredial.repository.BuildingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingMapper mapper;
    private final AddressRepository addressRepository;
    private final BuildingRepository buildingRepository;

    public Building create(BuildingRequestDTO dto) {
        var address = mapper.toEntity(dto.addressRequestDTO());
        var toSave = mapper.toEntity(dto);

        toSave.setAddress(address);

        return buildingRepository.save(toSave);
    }

    @Transactional
    public Building update(String id, BuildingRequestDTO dto) {

        var buildingToUpdate = findById(id);

        if (dto.addressRequestDTO() != null) {
            Address addressToUpdate = buildingToUpdate.getAddress();

            if (addressToUpdate == null) {
                addressToUpdate = mapper.toEntity(dto.addressRequestDTO());
                addressToUpdate = addressRepository.save(addressToUpdate);
                buildingToUpdate.setAddress(addressToUpdate);
            } else {
                mapper.updateAddressFromRequestDTO(dto.addressRequestDTO(), addressToUpdate);
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

    public List<BuildingResponseDTO> findAll(){
        List<Building> buildingList = buildingRepository.findAll();
        List<BuildingResponseDTO> responseDTOS = new LinkedList<>();

        for (Building building : buildingList) {
            var responseDTO = mapper.toResponseDTO(building);
            responseDTOS.add(responseDTO);
        }
        return responseDTOS;
    }
}
