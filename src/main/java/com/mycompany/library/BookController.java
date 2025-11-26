package com.mycompany.library;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Random;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML private Button button;
    @FXML private Label title;
    @FXML private Label author;
    @FXML private ImageView coverImage;
    @FXML private Label published;
    @FXML private TextFlow description;

    private Random random = new Random();
    private Gson gson = new Gson();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ Controller inicializado com GSON");
    }

    @FXML
    private void handleGenerateBook() {
        System.out.println("🎯 BOTÃO CLICADO! Buscando livro...");
        
        button.setDisable(true);
        button.setText("Buscando...");

        new Thread(() -> {
            try {
                Book livro = buscarLivroAleatorio();
                
                javafx.application.Platform.runLater(() -> {
                    preencherDados(livro);
                    button.setDisable(false);
                    button.setText("Draw a book");
                });
                
            } catch (Exception e) {
                System.out.println("❌ ERRO: " + e.getMessage());
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    title.setText("Erro ao buscar livro");
                    button.setDisable(false);
                    button.setText("Tentar Novamente");
                });
            }
        }).start();
    }

    private Book buscarLivroAleatorio() throws Exception {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=subject:fiction&maxResults=40";
        String jsonResponse = fazerRequisicaoAPI(apiUrl);
        return parseJsonParaLivro(jsonResponse);
    }

    private String fazerRequisicaoAPI(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erro HTTP: " + conn.getResponseCode());
        }

        StringBuilder response = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();
        conn.disconnect();

        return response.toString();
    }

    private Book parseJsonParaLivro(String json) {
        try {
            // Parse do JSON completo com GSON
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");
            
            // Escolhe livro aleatório
            int randomIndex = random.nextInt(items.size());
            JsonObject livroItem = items.get(randomIndex).getAsJsonObject();
            JsonObject volumeInfo = livroItem.getAsJsonObject("volumeInfo");
            
            // Extrai os dados de forma SIMPLES com GSON
            String titulo = volumeInfo.has("title") ? 
                volumeInfo.get("title").getAsString() : "Título não disponível";
            
            String autor = "Autor desconhecido";
            if (volumeInfo.has("authors")) {
                JsonArray authors = volumeInfo.getAsJsonArray("authors");
                autor = authors.get(0).getAsString();
            }
            
            String descricao = volumeInfo.has("description") ? 
                volumeInfo.get("description").getAsString() : "Descrição não disponível";
            
            String dataPublicacao = volumeInfo.has("publishedDate") ? 
                volumeInfo.get("publishedDate").getAsString() : "Data não informada";
            
            String capaUrl = "";
            if (volumeInfo.has("imageLinks")) {
                JsonObject imageLinks = volumeInfo.getAsJsonObject("imageLinks");
                capaUrl = imageLinks.has("thumbnail") ? 
                    imageLinks.get("thumbnail").getAsString().replace("\\/", "/") : "";
            }
            
            System.out.println("📊 GSON - Livro encontrado: " + titulo);
            
            return new Book(titulo, autor, descricao, capaUrl, dataPublicacao, "", "");
            
        } catch (Exception e) {
            System.out.println("❌ Erro no parse com GSON: " + e.getMessage());
            e.printStackTrace();
            return new Book("Erro", "Sistema", "Não foi possível carregar o livro", "", "", "", "");
        }
    }

    private void preencherDados(Book livro) {
        // Preenche os dados formatados
        title.setText("Título: " + livro.getTitle());
        author.setText("Autor: " + livro.getAuthor());
        published.setText("Publicado: " + livro.getPublishedDate());

        // Preenche a descrição no TextFlow
        String descricao = livro.getDescription();
        if (descricao.length() > 300) {
            descricao = descricao.substring(0, 300) + "...";
        }
        description.getChildren().setAll(new Text("Descrição: " + descricao));

        // Carrega a imagem
        if (!livro.getThumbnail().isEmpty()) {
            try {
                Image imagem = new Image(livro.getThumbnail(), 310, 289, true, true);
                coverImage.setImage(imagem);
            } catch (Exception e) {
                coverImage.setImage(null);
            }
        } else {
            coverImage.setImage(null);
        }
    }
}