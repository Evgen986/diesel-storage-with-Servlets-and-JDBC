package ru.maliutin.diesel.dto;

import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.entity.Technic;

public class MapperDtoImpl implements iMapperDto{

    @Override
    public Product toEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setTitle(productDTO.getTitle().strip());
        product.setCatalogueNumber(productDTO.getCatalogueNumber().strip());
        product.setProgramNumber(productDTO.getProgramNumber());
        product.setTechnics(productDTO.getTechnics()
                .stream().map(this::technicToEntity).toList());
        product.setBalance(productDTO.getBalance());
        product.setPrice(productDTO.getPrice());
        return product;
    }

    @Override
    public ProductDTO toDto(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle(product.getTitle());
        productDTO.setCatalogueNumber(product.getCatalogueNumber());
        productDTO.setProgramNumber(product.getProgramNumber());
        productDTO.setTechnics(product.getTechnics()
                .stream().map(this::technicToDto).toList());
        productDTO.setBalance(product.getBalance());
        productDTO.setPrice(product.getPrice());
        return productDTO;
    }

    private TechnicDTO technicToDto(Technic technic){
        return new TechnicDTO(technic.title());
    }

    private Technic technicToEntity(TechnicDTO technicDTO){
        return new Technic(technicDTO.title());
    }
}
