package com.project.bootcamp.service;
import com.project.bootcamp.exceptions.BusinessException;
import com.project.bootcamp.exceptions.NotFoundException;
import com.project.bootcamp.mapper.StockMapper;
import com.project.bootcamp.model.Stock;
import com.project.bootcamp.model.dto.StockDTO;
import com.project.bootcamp.repository.StockRepository;
import com.project.bootcamp.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {


    @Autowired
    private StockRepository repository;

    @Autowired
    private StockMapper mapper;


    /** Save recebe o que foi definido no repository que é o Stock, mas eu tenho que transformar isso na entity que é o que o banco entende
     * Essa transformação vai ser realizada por meio de uma camada "mapper"a parte
     **/
    @Transactional
    public StockDTO save(StockDTO dto) {

        /** é uma validaçao se já existe no bando de dados, find é select com name e date**/
        Optional<Stock> optionalStock = repository.findByNameAndDate(dto.getName(), dto.getDate());
        /** verificação**/
        if (optionalStock.isPresent()){
            throw new BusinessException(MessageUtil.STOCK_ALREADY_EXISTS);
        }

        Stock stock = mapper.toEntity(dto);
        repository.save(stock); /** aqui faz o insert da entity que acabou de mapear**/
        /**
         *  dto.setId(stock.getId());
         *  retornar o id para o front além do objeto**/
        return mapper.toDto(stock);
        /** vai retornar o maper que vai transformar a entity em DTO**/
    }

   @Transactional
    public StockDTO update(StockDTO dto) {
        Optional<Stock> optionalStock = repository.findByStockUpdate(dto.getName(), dto.getDate(), dto.getId());
        if (optionalStock.isPresent()){
            throw new BusinessException(MessageUtil.STOCK_ALREADY_EXISTS);
        }

        Stock stock = mapper.toEntity(dto);
        repository.save(stock);
        return mapper.toDto(stock);
    }


/** vai retornar uma lista nao apenas o stockdto
 *
 * **/
@Transactional(readOnly = true)
public List<StockDTO> findAll() {
        return mapper.toDto(repository.findAll());
    }

    @Transactional(readOnly = true)
    public StockDTO findById(Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public StockDTO delete(Long id) {
        StockDTO dto = this.findById(id);
        repository.deleteById(dto.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<StockDTO> findByToday() {
        return repository.findByToday(LocalDate.now()).map(mapper::toDto).orElseThrow(NotFoundException :: new);
    }
}
