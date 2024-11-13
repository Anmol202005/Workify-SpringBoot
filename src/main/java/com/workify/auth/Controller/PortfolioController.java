package com.workify.auth.Controller;

import com.workify.auth.models.Portfolio;
import com.workify.auth.models.dto.PortfolioDto;
import com.workify.auth.service.PortfolioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/create")
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody PortfolioDto portfolioDto, HttpServletRequest request) {
        return ResponseEntity.ok(portfolioService.createPortfolio(portfolioDto, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfolio(userId));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long userId, @RequestBody PortfolioDto portfolioDto) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(userId, portfolioDto));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long userId) {
        portfolioService.deletePortfolio(userId);
        return ResponseEntity.ok().build();
    }
}