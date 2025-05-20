import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ControleVendasApp extends Application {

    private TableView<Produto> tabelaProdutos;
    private TextField nomeInput, quantidadeInput, precoInput, vendasInput;
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Controle de Vendas - BloxImports");

        nomeInput = new TextField();
        nomeInput.setPromptText("Nome do Produto");

        quantidadeInput = new TextField();
        quantidadeInput.setPromptText("Quantidade");

        precoInput = new TextField();
        precoInput.setPromptText("Preço");

        vendasInput = new TextField();
        vendasInput.setPromptText("Vendas");

        Button adicionarBtn = new Button("Adicionar Produto");
        adicionarBtn.setOnAction(e -> adicionarProduto());

        Button relatorioBtn = new Button("Gerar Relatório de Vendas");
        relatorioBtn.setOnAction(e -> gerarRelatorio());

        HBox inputLayout = new HBox(10);
        inputLayout.getChildren().addAll(nomeInput, quantidadeInput, precoInput, vendasInput, adicionarBtn, relatorioBtn);

        tabelaProdutos = new TableView<>();
        tabelaProdutos.setPlaceholder(new Label("Nenhum produto cadastrado"));
        tabelaProdutos.setItems(listaProdutos);

        TableColumn<Produto, String> nomeCol = new TableColumn<>("Produto");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, Integer> qtdCol = new TableColumn<>("Quantidade");
        qtdCol.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<Produto, Double> precoCol = new TableColumn<>("Preço");
        precoCol.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Produto, Integer> vendasCol = new TableColumn<>("Vendas");
        vendasCol.setCellValueFactory(new PropertyValueFactory<>("vendas"));

        tabelaProdutos.setRowFactory(tv -> new TableRow<Produto>() {
            @Override
            protected void updateItem(Produto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getQuantidade() < 10) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tabelaProdutos.getColumns().addAll(nomeCol, qtdCol, precoCol, vendasCol);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(inputLayout, tabelaProdutos);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void adicionarProduto() {
        String nome = nomeInput.getText();
        int quantidade, vendas;
        double preco;

        try {
            quantidade = Integer.parseInt(quantidadeInput.getText());
            preco = Double.parseDouble(precoInput.getText());
            vendas = Integer.parseInt(vendasInput.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de entrada", "Quantidade, preço e vendas devem ser números válidos.");
            return;
        }

        if (nome.isEmpty()) {
            mostrarAlerta("Campo obrigatório", "O nome do produto não pode estar vazio.");
            return;
        }

        Produto produto = new Produto(nome, quantidade, preco, vendas);
        listaProdutos.add(produto);

        nomeInput.clear();
        quantidadeInput.clear();
        precoInput.clear();
        vendasInput.clear();
    }

    private void gerarRelatorio() {
        List<Produto> maisVendidos = listaProdutos.stream()
                .sorted(Comparator.comparingInt(Produto::getVendas).reversed())
                .limit(3)
                .collect(Collectors.toList());

        List<Produto> menosVendidos = listaProdutos.stream()
                .sorted(Comparator.comparingInt(Produto::getVendas))
                .limit(3)
                .collect(Collectors.toList());

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== PRODUTOS MAIS VENDIDOS ===\n");
        maisVendidos.forEach(p -> relatorio.append(p.getNome()).append(" - Vendas: ").append(p.getVendas()).append("\n"));

        relatorio.append("\n=== PRODUTOS MENOS VENDIDOS ===\n");
        menosVendidos.forEach(p -> relatorio.append(p.getNome()).append(" - Vendas: ").append(p.getVendas()).append("\n"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Relatório de Vendas");
        alert.setHeaderText(null);
        alert.setContentText(relatorio.toString());
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}

class Produto {
    private final StringProperty nome;
    private final IntegerProperty quantidade;
    private final DoubleProperty preco;
    private final IntegerProperty vendas;

    public Produto(String nome, int quantidade, double preco, int vendas) {
        this.nome = new SimpleStringProperty(nome);
        this.quantidade = new SimpleIntegerProperty(quantidade);
        this.preco = new SimpleDoubleProperty(preco);
        this.vendas = new SimpleIntegerProperty(vendas);
    }

    public String getNome() {
        return nome.get();
    }

    public int getQuantidade() {
        return quantidade.get();
    }

    public double getPreco() {
        return preco.get();
    }

    public int getVendas() {
        return vendas.get();
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public IntegerProperty quantidadeProperty() {
        return quantidade;
    }

    public DoubleProperty precoProperty() {
        return preco;
    }

    public IntegerProperty vendasProperty() {
        return vendas;
    }
}