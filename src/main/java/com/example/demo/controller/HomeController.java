package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.service.impl.ClientServiceImpl;
import com.example.demo.service.ArticleService;
import com.example.demo.service.FactureService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Controller principale pour affichage des clients / factures sur la page d'acceuil.
 */
@Controller
public class HomeController {

    private static final int COL_SURNAME = 0;
    private static final int COL_FIRSTNAME = 1;
    private static final int COL_AGE = 2;

    private ArticleService articleService;
    private ClientServiceImpl clientServiceImpl;
    private FactureService factureService;

    public HomeController(ArticleService articleService, ClientServiceImpl clientService, FactureService factureService) {
        this.articleService = articleService;
        this.clientServiceImpl = clientService;
        this.factureService = factureService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("home");

        List<Article> articles = articleService.findAll();
        modelAndView.addObject("articles", articles);

        List<Client> toto = clientServiceImpl.findAllClients();
        modelAndView.addObject("clients", toto);

        List<Facture> factures = factureService.findAllFactures();
        modelAndView.addObject("factures", factures);

        return modelAndView;
    }

    // Export tous les articles en csv
    @GetMapping("/articles/csv")
    public void articlesCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachement: filename\"export-article.csv\"");

        PrintWriter writer = response.getWriter();
        List<Article>articles = articleService.findAll();
        String header = "Libelle;Prix";
        writer.println(header);
        for (Article article : articles) {
            String line = article.getLibelle() + ";" + article.getPrix();
            writer.println(line);
        }

        writer.println();
    }

    // Export tous les clients en CVS en ajoutant une nouvelle colonne : age du client
    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachement: file\"export-client.csv\"");

        PrintWriter writer = response.getWriter();
        List<Client> clients = clientServiceImpl.findAllClients();
        String header = "Nom;Prénom;Age";
        writer.println(header);
        for (Client client : clients) {
            String line = client.getNom() + ";" + client.getPrenom() + ";" + client.getAge();
            writer.println(line);
        }
    }

    // Export tous les clients en XLXS en ajoutant une nouvelle colonne : age du client
    @GetMapping("clients/xlsx")
    public void clientsXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xlsx");
        response.setHeader("Content-Disposition", "attachement: file\"export-client.xlsx\"");

        String[] columns = {"Name", "Prénom", "Age"};

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");
        Row headerRow = sheet.createRow(0);

        // header
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        List<Client> clients = clientServiceImpl.findAllClients();
        for(Client client: clients) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(COL_SURNAME).setCellValue(client.getPrenom());
            row.createCell(COL_FIRSTNAME).setCellValue(client.getNom());
            row.createCell(COL_AGE).setCellValue(client.getAge());
        }

        CellStyle cell = workbook.createCellStyle();
        Font font = workbook.createFont();

        // Resize all columns to fit the content size
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOutputStream = new FileOutputStream("clients.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }

    // Export factures par Client
    @GetMapping("clients/{idClient}/factures/xlsx")
    public void facturesClientsXLSX(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable(value = "idClient") Long idClient
    ) {
        Client client = clientServiceImpl.findClient(idClient);

    }
}
