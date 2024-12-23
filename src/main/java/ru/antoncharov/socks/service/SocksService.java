package ru.antoncharov.socks.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.antoncharov.socks.domain.Socks;
import ru.antoncharov.socks.dto.SocksDto;
import ru.antoncharov.socks.exception.NotEnoughSocksException;
import ru.antoncharov.socks.exception.WrongCSVException;
import ru.antoncharov.socks.repository.SocksRepository;
import ru.antoncharov.socks.specification.SocksSpecification;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SocksService {

    private static final Logger logger = LoggerFactory.getLogger(SocksService.class);

    private final SocksRepository sockRepository;

    public void addSocks(SocksDto sockDto) {
        final Socks sock = new Socks();
        sock.setColor(sockDto.color());
        sock.setCottonPercentage(sockDto.cottonPercentage());
        sock.setQuantity(sockDto.quantity());

        sockRepository.save(sock);
        logger.info("Added socks with color {}, cotton percentage {} and quantity {}", sock.getColor(), sock.getCottonPercentage(), sock.getQuantity());
    }

    @Transactional
    public void removeSocks(SocksDto sockDto) throws NotEnoughSocksException {
        Optional<Socks> optionalSock = findMatchingSock(sockDto);
        if (optionalSock.isPresent()) {
            Socks sock = optionalSock.get();
            if (sock.getQuantity() >= sockDto.quantity()) {
                sock.setQuantity(sock.getQuantity() - sockDto.quantity());
                sockRepository.save(sock);
                logger.info("Removed socks with color {}, cotton percentage {} and quantity {}", sock.getColor(), sock.getCottonPercentage(), sockDto.quantity());
            } else {
                throw new NotEnoughSocksException("Not enough socks in stock");
            }
        } else {
            throw new NotEnoughSocksException("No matching socks found");
        }
    }

    public List<Socks> getSocks(String color, Integer cottonPercentage, String comparisonOperator) {
        return sockRepository.findAll(SocksSpecification.toPredicate(comparisonOperator, cottonPercentage).and(SocksSpecification.byColor(color)));
    }

    public void updateSock(Long id, SocksDto sockDto) {
        Optional<Socks> optionalSock = sockRepository.findById(id);
        if (optionalSock.isPresent()) {
            Socks sock = optionalSock.get();
            sock.setColor(sockDto.color());
            sock.setCottonPercentage(sockDto.cottonPercentage());
            sock.setQuantity(sockDto.quantity());

            sockRepository.save(sock);
            logger.info("Updated socks with ID {}", id);
        } else {
            logger.error("Sock with ID {} not found", id);
        }
    }

    private Optional<Socks> findMatchingSock(SocksDto sockDto) {
        return sockRepository.findFirstByColorAndCottonPercentage(sockDto.color(), sockDto.cottonPercentage());
    }

    public void batchFromFile(MultipartFile file) throws IOException {
        Reader reader = new InputStreamReader(file.getInputStream());
        try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length != 3) {
                    throw new WrongCSVException("Wrong format");
                }
                addSocks(new SocksDto(line[0], Integer.getInteger(line[1]), Integer.getInteger(line[2])));
            }
        } catch (CsvValidationException e) {
            throw new WrongCSVException("Wrong format");
        }
    }
}
