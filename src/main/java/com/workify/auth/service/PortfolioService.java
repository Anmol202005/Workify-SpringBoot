package com.workify.auth.service;

import com.workify.auth.models.Portfolio;
import com.workify.auth.models.User;
import com.workify.auth.models.dto.PortfolioDto;
import com.workify.auth.repository.PortfolioRepository;
import com.workify.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    public Portfolio createPortfolio(PortfolioDto portfolioDto, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Portfolio portfolio = new Portfolio();
            portfolio.setUser(user.get());
            portfolio.setTitle(portfolioDto.getTitle());
            portfolio.setDescription(portfolioDto.getDescription());
            portfolio.setWebsite(portfolioDto.getWebsite());
            portfolio.setGithub(portfolioDto.getGithub());
            portfolio.setLinkedin(portfolioDto.getLinkedin());
            return portfolioRepository.save(portfolio);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Portfolio getPortfolio(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public Portfolio updatePortfolio(Long userId, PortfolioDto portfolioDto) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId);
        if (portfolio != null) {
            portfolio.setTitle(portfolioDto.getTitle());
            portfolio.setDescription(portfolioDto.getDescription());
            portfolio.setWebsite(portfolioDto.getWebsite());
            portfolio.setGithub(portfolioDto.getGithub());
            portfolio.setLinkedin(portfolioDto.getLinkedin());
            return portfolioRepository.save(portfolio);
        } else {
            throw new RuntimeException("Portfolio not found");
        }
    }

    public void deletePortfolio(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId);
        if (portfolio != null) {
            portfolioRepository.delete(portfolio);
        } else {
            throw new RuntimeException("Portfolio not found");
        }
    }
}