import java.util.List;
import java.util.Arrays;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class AKDMainDashboard extends Application {

    private TabPane MainTabs;
    private TabPane ProfileTabs;
    private TableView<MarketRow> MarketTable;
    private TableView<PortfolioRow> PortfolioTable;
    private BorderPane RootPane;

    @Override
    public void start(Stage PrimaryStage) {
        PrimaryStage.setTitle("AKD TradeCast - Market Monitor");

        RootPane = new BorderPane();
        RootPane.setStyle("-fx-background-color: #F0F0F0;");

        RootPane.setTop(BuildTopBar());
        RootPane.setCenter(BuildMainContent());
        RootPane.setBottom(BuildStatusBar());

        Scene MainScene = new Scene(RootPane, 1100, 650);
        PrimaryStage.setScene(MainScene);
        PrimaryStage.show();
    }

    private VBox BuildTopBar() {
        VBox TopSection = new VBox();
        TopSection.getChildren().addAll(BuildMenuBar(), BuildTitleBar());
        return TopSection;
    }

    private MenuBar BuildMenuBar() {
        MenuBar TopMenuBar = new MenuBar();

        Menu ServicesMenu = new Menu("Services");
        MenuItem WithdrawalStatementItem = new MenuItem("Cash Withdrawal Statement");
        WithdrawalStatementItem.setOnAction(E -> ShowWithdrawalStatementWindow());
        MenuItem WithdrawalRequestItem = new MenuItem("Cash Withdrawal Request");
        WithdrawalRequestItem.setOnAction(E -> ShowWithdrawalRequestWindow());
        ServicesMenu.getItems().addAll(WithdrawalStatementItem, WithdrawalRequestItem);

        Menu ToolsMenu = new Menu("Tools");
        MenuItem RestoreLayoutItem = new MenuItem("Restore Default Layout");
        RestoreLayoutItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        RestoreLayoutItem.setOnAction(E -> RestoreDefaultLayout());
        ToolsMenu.getItems().add(RestoreLayoutItem);

        TopMenuBar.getMenus().addAll(ServicesMenu, ToolsMenu);
        return TopMenuBar;
    }

    private HBox BuildTitleBar() {
        HBox TitleBar = new HBox();
        TitleBar.setStyle("-fx-background-color: #3366CC; -fx-padding: 5 10 5 10;");
        TitleBar.setAlignment(Pos.CENTER_LEFT);

        Text BrandText = new Text("AKD TradeCast");
        BrandText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        BrandText.setFill(Color.WHITE);

        Region Spacer = new Region();
        HBox.setHgrow(Spacer, Priority.ALWAYS);

        Text AkdText = new Text("akd trade.com");
        AkdText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        AkdText.setFill(Color.web("#FFD700"));

        TitleBar.getChildren().addAll(BrandText, Spacer, AkdText);
        return TitleBar;
    }

    private BorderPane BuildMainContent() {
        BorderPane ContentPane = new BorderPane();

        MainTabs = new TabPane();
        MainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab WatchesTab = new Tab("My Watches");
        WatchesTab.setContent(BuildWatchesContent());
        Tab TradeLogTab = new Tab("Trade Log");
        TradeLogTab.setContent(BuildOrderHistoryView());
        Tab OutstandingTab = new Tab("Outstanding Log");
        OutstandingTab.setContent(BuildPlaceholderPane("Outstanding Log"));
        Tab ActivityTab = new Tab("Activity Log");
        ActivityTab.setContent(BuildPlaceholderPane("Activity Log"));

        MainTabs.getTabs().addAll(WatchesTab, TradeLogTab, OutstandingTab, ActivityTab);

        VBox RightPanel = new VBox(10);
        RightPanel.setPrefWidth(350);
        RightPanel.getChildren().addAll(BuildMarketDepthView(), BuildMessageCenter());

        SplitPane MainSplit = new SplitPane();
        MainSplit.getItems().addAll(MainTabs, RightPanel);
        MainSplit.setDividerPositions(0.7);

        ContentPane.setCenter(MainSplit);
        return ContentPane;
    }

    private BorderPane BuildWatchesContent() {
        BorderPane WatchesPane = new BorderPane();

        ProfileTabs = new TabPane();
        ProfileTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab FirstProfile = new Tab("1st Profile");
        FirstProfile.setContent(BuildMarketView());
        Tab SecondProfile = new Tab("2nd Profile");
        SecondProfile.setContent(BuildPlaceholderPane("2nd Profile"));
        Tab ThirdProfile = new Tab("3rd Profile");
        ThirdProfile.setContent(BuildPlaceholderPane("3rd Profile"));
        Tab SectorWatch = new Tab("Sector Watch");
        SectorWatch.setContent(BuildPlaceholderPane("Sector Watch"));
        Tab IndexWatch = new Tab("Index Watch");
        IndexWatch.setContent(BuildPlaceholderPane("Index Watch"));
        Tab SpotWatch = new Tab("Spot Watch");
        SpotWatch.setContent(BuildPlaceholderPane("Spot Watch"));
        Tab FutureWatch = new Tab("Future Watch");
        FutureWatch.setContent(BuildPlaceholderPane("Future Watch"));
        Tab CapWatch = new Tab("Cap Watch");
        CapWatch.setContent(BuildPlaceholderPane("Cap Watch"));
        Tab PortfolioWatch = new Tab("Portfolio Watch");
        PortfolioWatch.setContent(BuildPortfolioView());

        ProfileTabs.getTabs().addAll(
            FirstProfile, SecondProfile, ThirdProfile, SectorWatch,
            IndexWatch, SpotWatch, FutureWatch, CapWatch, PortfolioWatch
        );

        WatchesPane.setCenter(ProfileTabs);
        return WatchesPane;
    }

    private VBox BuildMarketView() {
        VBox MarketPane = new VBox(5);
        MarketPane.setPadding(new Insets(5));

        HBox SearchRow = BuildScripSearchRow();
        MarketTable = CreateMarketTable();
        PopulateMarketData();

        VBox.setVgrow(MarketTable, Priority.ALWAYS);
        MarketPane.getChildren().addAll(SearchRow, MarketTable);
        return MarketPane;
    }

    private HBox BuildScripSearchRow() {
        HBox Row = new HBox(8);
        Row.setAlignment(Pos.CENTER_LEFT);
        Row.setPadding(new Insets(3, 5, 3, 5));
        Row.setStyle("-fx-background-color: #E8E8E8; -fx-border-color: #CCCCCC; -fx-border-width: 0 0 1 0;");

        Label ScripBrowse = new Label("Scrip Browse");
        ScripBrowse.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        Label ScripLabel = new Label("Scrip");
        ScripLabel.setFont(Font.font("Arial", 11));

        TextField ScripField = new TextField();
        ScripField.setPrefWidth(80);
        ScripField.setPromptText("Symbol");

        Region Spacer = new Region();
        HBox.setHgrow(Spacer, Priority.ALWAYS);

        Label SectorInfo = new Label("Engro Foods Limited. - Sector");
        SectorInfo.setFont(Font.font("Arial", 11));
        SectorInfo.setTextFill(Color.web("#333333"));

        Region Spacer2 = new Region();
        HBox.setHgrow(Spacer2, Priority.ALWAYS);

        Button SaveProfile = new Button("Save Profile");
        SaveProfile.setStyle("-fx-font-size: 11;");

        Row.getChildren().addAll(ScripBrowse, ScripLabel, ScripField, Spacer, SectorInfo, Spacer2, SaveProfile);
        return Row;
    }

    @SuppressWarnings("unchecked")
    private TableView<MarketRow> CreateMarketTable() {
        TableView<MarketRow> Table = new TableView<>();
        Table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        Table.setStyle("-fx-font-size: 11;");

        TableColumn<MarketRow, String> MktCol = MakeStringColumn("Mkt", "Mkt", 40);
        TableColumn<MarketRow, String> ScripCol = MakeStringColumn("Scrip", "Scrip", 60);
        TableColumn<MarketRow, Double> LastPriceCol = MakeDoubleColumn("LastPrice", "LastPrice", 65);
        TableColumn<MarketRow, Double> ChangeCol = MakeDoubleColumn("Change", "Change", 55);
        TableColumn<MarketRow, Double> BuyCol = MakeDoubleColumn("Buy", "Buy", 55);
        TableColumn<MarketRow, Integer> BVolCol = MakeIntColumn("BVol", "BVol", 55);
        TableColumn<MarketRow, Double> SellCol = MakeDoubleColumn("Sell", "Sell", 55);
        TableColumn<MarketRow, Integer> SVolCol = MakeIntColumn("SVol", "SVol", 60);
        TableColumn<MarketRow, Double> HighCol = MakeDoubleColumn("High", "High", 55);
        TableColumn<MarketRow, Double> LowCol = MakeDoubleColumn("Low", "Low", 55);
        TableColumn<MarketRow, Integer> TotalVolCol = MakeIntColumn("TotalVolume", "TotalVolume", 80);
        TableColumn<MarketRow, Double> AvgCol = MakeDoubleColumn("Average", "Average", 60);
        TableColumn<MarketRow, Double> ClosePriceCol = MakeDoubleColumn("ClosePrice", "ClosePrice", 65);
        TableColumn<MarketRow, Void> TradeCol = BuildTradeButtonColumn();

        Table.getColumns().addAll(
            MktCol, ScripCol, LastPriceCol, ChangeCol, BuyCol, BVolCol,
            SellCol, SVolCol, HighCol, LowCol, TotalVolCol, AvgCol, ClosePriceCol, TradeCol
        );

        ApplyChangeColumnStyle(ChangeCol);
        return Table;
    }

    private TableColumn<MarketRow, Void> BuildTradeButtonColumn() {
        TableColumn<MarketRow, Void> TradeCol = new TableColumn<>("Trade");
        TradeCol.setPrefWidth(55);
        TradeCol.setCellFactory(Col -> new TableCell<>() {
            private final Button TradeBtn = new Button("Trade");
            {
                TradeBtn.setStyle("-fx-font-size: 10; -fx-padding: 1 6 1 6;");
                TradeBtn.setOnAction(E -> {
                    MarketRow Row = getTableView().getItems().get(getIndex());
                    ShowTradeAlert(Row.getScrip());
                });
            }
            @Override
            protected void updateItem(Void Item, boolean Empty) {
                super.updateItem(Item, Empty);
                setGraphic(Empty ? null : TradeBtn);
            }
        });
        return TradeCol;
    }

    private void ApplyChangeColumnStyle(TableColumn<MarketRow, Double> Col) {
        Col.setCellFactory(Column -> new TableCell<>() {
            @Override
            protected void updateItem(Double Value, boolean Empty) {
                super.updateItem(Value, Empty);
                if (Empty || Value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", Value));
                    if (Value < 0) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else if (Value > 0) {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });
    }

    private <T> TableColumn<T, String> MakeStringColumn(String Header, String Property, double Width) {
        TableColumn<T, String> Col = new TableColumn<>(Header);
        Col.setCellValueFactory(new PropertyValueFactory<>(Property));
        Col.setPrefWidth(Width);
        return Col;
    }

    private <T> TableColumn<T, Double> MakeDoubleColumn(String Header, String Property, double Width) {
        TableColumn<T, Double> Col = new TableColumn<>(Header);
        Col.setCellValueFactory(new PropertyValueFactory<>(Property));
        Col.setPrefWidth(Width);
        return Col;
    }

    private <T> TableColumn<T, Integer> MakeIntColumn(String Header, String Property, double Width) {
        TableColumn<T, Integer> Col = new TableColumn<>(Header);
        Col.setCellValueFactory(new PropertyValueFactory<>(Property));
        Col.setPrefWidth(Width);
        return Col;
    }

    private void PopulateMarketData() {
        ObservableList<MarketRow> Data = FXCollections.observableArrayList(
            new MarketRow("REG", "NBP", 41.30, -2.17, 0, 0, 41.30, 770918, 41.31, 41.30, 1244519, 41.30, 43.47),
            new MarketRow("REG", "ENGRO", 112.50, -5.22, 112.45, 1, 112.50, 6611, 114.41, 111.86, 662694, 112.60, 117.72),
            new MarketRow("REG", "OGDC", 125.25, -3.72, 125.01, 232, 125.25, 110, 126.00, 124.15, 124480, 125.05, 128.97),
            new MarketRow("REG", "LOTPTA", 10.70, -0.43, 10.69, 390, 10.70, 98775, 10.85, 10.57, 1233750, 10.73, 11.13),
            new MarketRow("REG", "DGKC", 20.00, -0.63, 20.05, 5000, 20.10, 2500, 20.39, 20.00, 201761, 20.07, 20.63),
            new MarketRow("REG", "LUCK", 71.00, -0.93, 71.01, 2000, 71.10, 4172, 71.25, 70.60, 68389, 71.01, 71.93),
            new MarketRow("REG", "NCL", 16.55, -0.72, 16.50, 7595, 16.55, 62, 16.99, 16.45, 41958, 16.53, 17.27),
            new MarketRow("REG", "NML", 40.35, -1.66, 40.30, 270, 40.35, 1035, 41.05, 40.05, 136098, 40.31, 42.01),
            new MarketRow("REG", "EFOODS", 22.90, -0.20, 22.90, 107, 22.95, 2000, 23.00, 22.25, 11105, 22.87, 23.10),
            new MarketRow("REG", "AKBL", 9.25, -0.32, 9.25, 145, 9.40, 1000, 9.52, 9.25, 19100, 9.32, 9.57),
            new MarketRow("REG", "BAFL", 9.80, -0.16, 9.80, 613, 9.84, 50, 9.81, 9.70, 168537, 9.73, 9.96),
            new MarketRow("REG", "GGL", 10.70, -0.25, 10.70, 172, 10.95, 5000, 10.70, 10.70, 10, 10.70, 10.95),
            new MarketRow("REG", "ANL", 4.50, -0.19, 4.48, 200, 4.50, 54, 4.59, 4.33, 601627, 4.48, 4.69),
            new MarketRow("REG", "ATRL", 113.65, -3.71, 113.65, 2273, 113.74, 2000, 114.55, 111.50, 361347, 113.21, 117.36),
            new MarketRow("REG", "PRL", 58.48, -3.07, 0, 0, 58.48, 9929, 59.88, 58.48, 15577, 58.64, 61.55),
            new MarketRow("REG", "NRL", 354.49, -7.20, 354.50, 32, 354.68, 200, 355.49, 348.00, 77493, 352.37, 361.69)
        );
        MarketTable.setItems(Data);
    }

    private void ShowTradeAlert(String Scrip) {
        Alert TradeDialog = new Alert(Alert.AlertType.INFORMATION);
        TradeDialog.setTitle("Trade");
        TradeDialog.setHeaderText("Trade - " + Scrip);
        TradeDialog.setContentText("Trade dialog for " + Scrip + " would open here.");
        TradeDialog.showAndWait();
    }

    private VBox BuildPortfolioView() {
        VBox PortfolioPane = new VBox(5);
        PortfolioPane.setPadding(new Insets(5));

        HBox TopSection = new HBox(15);
        TopSection.setPadding(new Insets(5));

        VBox ScripWorth = BuildScripWorthPanel();
        VBox SecurityHoldings = BuildSecurityHoldingsPanel();
        VBox PortfolioWorth = BuildPortfolioWorthPanel();

        HBox.setHgrow(SecurityHoldings, Priority.ALWAYS);
        TopSection.getChildren().addAll(ScripWorth, SecurityHoldings, PortfolioWorth);

        PortfolioTable = CreatePortfolioTable();
        PopulatePortfolioData();
        VBox.setVgrow(PortfolioTable, Priority.ALWAYS);

        HBox ActionRow = BuildLiquidateRow();

        PortfolioPane.getChildren().addAll(TopSection, PortfolioTable, ActionRow);
        return PortfolioPane;
    }

    private HBox BuildLiquidateRow() {
        HBox ActionRow = new HBox(10);
        ActionRow.setAlignment(Pos.CENTER_LEFT);
        ActionRow.setPadding(new Insets(5, 0, 0, 0));

        Button LiquidateBtn = new Button("Liquidate All Security Holdings");
        LiquidateBtn.setStyle("-fx-font-size: 11; -fx-text-fill: #CC0000;");
        LiquidateBtn.setOnAction(E -> {
            Alert Confirm = new Alert(Alert.AlertType.CONFIRMATION);
            Confirm.setTitle("Confirm Liquidation");
            Confirm.setHeaderText("Liquidate All Holdings");
            Confirm.setContentText("Are you sure you want to liquidate all security holdings?");
            Confirm.showAndWait();
        });

        ActionRow.getChildren().add(LiquidateBtn);
        return ActionRow;
    }

    private VBox BuildScripWorthPanel() {
        VBox Panel = new VBox(4);
        Panel.setPadding(new Insets(8));
        Panel.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #FAFAFA;");

        Label Title = new Label("Scrip Worth");
        Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Panel.getChildren().add(Title);
        AddInfoRow(Panel, "Net Quantity", "220");
        AddInfoRow(Panel, "Avg. Price", "Rs. 145.75");
        AddInfoRow(Panel, "Investment", "Rs. 32,065.00");
        AddInfoRow(Panel, "Current Price", "Rs. 135.21");
        AddInfoRow(Panel, "Current Worth", "Rs. 29,746.20");
        AddInfoRow(Panel, "Profit / Loss", "Rs. 2,318.80");

        return Panel;
    }

    private VBox BuildSecurityHoldingsPanel() {
        VBox Panel = new VBox(4);
        Panel.setPadding(new Insets(8));
        Panel.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #FAFAFA;");

        Label Title = new Label("Security Holdings");
        Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Panel.getChildren().add(Title);
        AddInfoRow(Panel, "Investment", "Rs. 443,952.81");
        AddInfoRow(Panel, "Current Worth", "Rs. 527,003.24");
        AddInfoRow(Panel, "Profit / Loss", "Rs. 83,050.43");

        return Panel;
    }

    private VBox BuildPortfolioWorthPanel() {
        VBox Panel = new VBox(4);
        Panel.setPadding(new Insets(8));
        Panel.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #FAFAFA;");

        Label Title = new Label("Portfolio Worth");
        Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Panel.getChildren().add(Title);
        AddInfoRow(Panel, "Collaterals", "Rs. 527,003.24");
        AddInfoRow(Panel, "Ledger Balance", "Rs. 27.00");
        AddInfoRow(Panel, "Profit / Loss", "Rs. 0.00");
        AddInfoRow(Panel, "Sold Collaterals", "Rs. 0.00");
        AddInfoRow(Panel, "Net Worth", "Rs. 527,030.24");

        return Panel;
    }

    private void AddInfoRow(VBox Container, String LabelText, String ValueText) {
        HBox Row = new HBox(8);
        Row.setAlignment(Pos.CENTER_LEFT);

        Label Lbl = new Label(LabelText);
        Lbl.setFont(Font.font("Arial", 11));
        Lbl.setPrefWidth(110);

        TextField Val = new TextField(ValueText);
        Val.setEditable(false);
        Val.setPrefWidth(120);
        Val.setStyle("-fx-font-size: 11; -fx-background-color: #EEEEEE;");

        Row.getChildren().addAll(Lbl, Val);
        Container.getChildren().add(Row);
    }

    @SuppressWarnings("unchecked")
    private TableView<PortfolioRow> CreatePortfolioTable() {
        TableView<PortfolioRow> Table = new TableView<>();
        Table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        Table.setStyle("-fx-font-size: 11;");

        Table.getColumns().addAll(
            MakeStringColumn("Mkt", "Mkt", 40),
            MakeStringColumn("Scrip", "Scrip", 60),
            MakeIntColumn("Net Qty", "NetQty", 55),
            MakeDoubleColumn("Avg Price", "AvgPrice", 65),
            MakeDoubleColumn("Change", "Change", 55),
            MakeDoubleColumn("Average", "Average", 60),
            MakeIntColumn("BVol", "BVol", 55),
            MakeDoubleColumn("Buy", "Buy", 55),
            MakeDoubleColumn("Sell", "Sell", 55),
            MakeIntColumn("SVol", "SVol", 55),
            MakeDoubleColumn("LastPrice", "LastPrice", 65),
            MakeIntColumn("TotalVolume", "TotalVolume", 80),
            MakeDoubleColumn("High", "High", 55),
            MakeDoubleColumn("Low", "Low", 55),
            MakeDoubleColumn("ClosePrice", "ClosePrice", 65),
            MakeStringColumn("State", "State", 50)
        );

        return Table;
    }

    private void PopulatePortfolioData() {
        ObservableList<PortfolioRow> Data = FXCollections.observableArrayList(
            new PortfolioRow("REG", "BAHL", 915, 26.24, 0.20, 29.11, 2500, 29.26, 29.29, 99, 29.29, 191380, 29.30, 28.92, 29.28, "OPN"),
            new PortfolioRow("REG", "EFUL", 566, 78.00, 3.16, 67.83, 34, 67.10, 68.37, 100, 68.39, 356, 68.39, 67.10, 65.23, "OPN"),
            new PortfolioRow("REG", "ENGRO", 220, 145.75, 2.17, 135.67, 500, 135.22, 135.30, 8, 135.21, 2722861, 137.30, 133.30, 135.35, "OPN"),
            new PortfolioRow("REG", "GAIL", 3500, 4.40, 0.00, 0, 726, 3.11, 3.45, 1500, 0, 0, 0, 0, 3.21, "OPN"),
            new PortfolioRow("REG", "HBL", 319, 89.39, 0.40, 117.21, 1795, 117.50, 117.59, 100, 117.50, 28250, 117.70, 116.50, 117.10, "OPN"),
            new PortfolioRow("REG", "HMB", 870, 19.67, 0.07, 17.96, 5000, 17.86, 17.97, 8500, 17.85, 10818, 18.00, 17.84, 17.78, "OPN"),
            new PortfolioRow("REG", "HUBC", 1000, 32.03, 0.32, 40.25, 5865, 40.30, 40.33, 1000, 40.30, 3252180, 40.40, 40.06, 39.98, "OPN"),
            new PortfolioRow("REG", "JSCL", 1031, 12.50, 0.08, 5.78, 2000, 5.71, 5.72, 3950, 5.70, 1104465, 5.89, 5.69, 5.70, "OPN"),
            new PortfolioRow("REG", "KESC", 3000, 2.27, -0.01, 1.89, 2848, 1.89, 1.92, 2, 1.89, 148531, 1.94, 1.83, 1.91, "OPN"),
            new PortfolioRow("REG", "LOTPTA", 1000, 8.05, 0.01, 12.92, 78314, 12.78, 12.79, 10000, 12.79, 3502276, 13.01, 12.76, 12.80, "OPN"),
            new PortfolioRow("REG", "NIB", 4000, 3.06, 0.00, 1.40, 1, 1.39, 1.40, 9593, 1.40, 420032, 1.44, 1.35, 1.36, "OPN"),
            new PortfolioRow("REG", "NRJC", 3500, 39.00, 1.45, 54.69, 16, 54.95, 55.00, 1000, 54.95, 9068, 55.45, 53.50, 54.95, "OPN")
        );
        PortfolioTable.setItems(Data);
    }

    private VBox BuildOrderHistoryView() {
        VBox Container = new VBox(5);
        Container.setPadding(new Insets(5));

        GridPane Filters = BuildOrderHistoryFilters();
        TableView<OrderHistoryRow> HistoryTable = CreateOrderHistoryTable();
        HBox BottomStatusBar = BuildOrderHistoryStatusBar();

        ObservableList<OrderHistoryRow> HistoryData = FXCollections.observableArrayList();
        HistoryTable.setItems(HistoryData);

        Label RecordLabel = (Label) BottomStatusBar.getChildren().get(BottomStatusBar.getChildren().size() - 1);

        Button GetBtn = (Button) Filters.getChildren().stream()
            .filter(Node -> Node instanceof Button)
            .findFirst().orElse(null);

        if (GetBtn != null) {
            GetBtn.setOnAction(E -> {
                HistoryData.clear();
                HistoryData.addAll(GetMockOrderHistory());
                RecordLabel.setText("Records Found: " + HistoryData.size());
            });
        }

        HistoryData.addAll(GetMockOrderHistory());
        RecordLabel.setText("Records Found: " + HistoryData.size());

        Container.getChildren().addAll(Filters, HistoryTable, BottomStatusBar);
        VBox.setVgrow(HistoryTable, Priority.ALWAYS);

        return Container;
    }

    private GridPane BuildOrderHistoryFilters() {
        GridPane Filters = new GridPane();
        Filters.setHgap(8);
        Filters.setVgap(8);
        Filters.setPadding(new Insets(5));
        Filters.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;");

        Filters.add(new Label("Account No:"), 0, 0);
        TextField AccountNoField = new TextField();
        AccountNoField.setPromptText("Account No");
        Filters.add(AccountNoField, 1, 0);

        Filters.add(new Label("Name:"), 2, 0);
        TextField NameField = new TextField();
        NameField.setPromptText("Name");
        Filters.add(NameField, 3, 0);

        Filters.add(new Label("Type:"), 4, 0);
        ComboBox<String> TypeCombo = new ComboBox<>();
        TypeCombo.getItems().addAll("All", "Buy", "Sell");
        TypeCombo.setValue("All");
        Filters.add(TypeCombo, 5, 0);

        Filters.add(new Label("Scrip:"), 0, 1);
        TextField ScripField = new TextField();
        ScripField.setPromptText("Symbol");
        Filters.add(ScripField, 1, 1);

        Filters.add(new Label("Start Date:"), 2, 1);
        DatePicker StartDatePicker = new DatePicker();
        StartDatePicker.setValue(LocalDate.now().minusMonths(1));
        Filters.add(StartDatePicker, 3, 1);

        Filters.add(new Label("End Date:"), 4, 1);
        DatePicker EndDatePicker = new DatePicker();
        EndDatePicker.setValue(LocalDate.now());
        Filters.add(EndDatePicker, 5, 1);

        Button GetBtn = new Button("Get");
        GetBtn.setStyle("-fx-font-weight: bold;");
        Filters.add(GetBtn, 6, 1);

        return Filters;
    }

    @SuppressWarnings("unchecked")
    private TableView<OrderHistoryRow> CreateOrderHistoryTable() {
        TableView<OrderHistoryRow> Table = new TableView<>();
        Table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        Table.setStyle("-fx-font-size: 11;");

        Table.getColumns().addAll(
            MakeStringColumn("Scrip", "Scrip", 80),
            MakeIntColumn("Quantity", "Quantity", 70),
            MakeDoubleColumn("Gross Rate", "GrossRate", 80),
            MakeDoubleColumn("Net Amount", "NetAmount", 90),
            MakeStringColumn("Clearing Date", "ClearingDate", 100)
        );

        return Table;
    }

    private HBox BuildOrderHistoryStatusBar() {
        HBox StatusBar = new HBox(10);
        StatusBar.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 5;");
        StatusBar.setAlignment(Pos.CENTER_LEFT);

        Label DateLabel = new Label("Date: " + LocalDate.now());
        Region Spacer = new Region();
        HBox.setHgrow(Spacer, Priority.ALWAYS);
        Label RecordLabel = new Label("Records Found: 0");

        StatusBar.getChildren().addAll(DateLabel, Spacer, RecordLabel);
        return StatusBar;
    }

    private List<OrderHistoryRow> GetMockOrderHistory() {
        return Arrays.asList(
            new OrderHistoryRow("ENGRO", 500, 112.50, 56250.00, "2024-04-15"),
            new OrderHistoryRow("NBP", 1000, 41.30, 41300.00, "2024-04-14"),
            new OrderHistoryRow("OGDC", 200, 125.25, 25050.00, "2024-04-13"),
            new OrderHistoryRow("LOTPTA", 5000, 10.70, 53500.00, "2024-04-12"),
            new OrderHistoryRow("DGKC", 3000, 20.00, 60000.00, "2024-04-11")
        );
    }

    private void ShowWithdrawalStatementWindow() {
        Stage WithdrawalStage = new Stage();
        WithdrawalStage.setTitle("Cash Withdrawal Statement");
        WithdrawalStage.setResizable(false);

        VBox Content = BuildWithdrawalStatementView();
        Scene WithdrawalScene = new Scene(Content, 800, 500);
        WithdrawalStage.setScene(WithdrawalScene);
        WithdrawalStage.show();
    }

    private VBox BuildWithdrawalStatementView() {
        VBox Container = new VBox(5);
        Container.setPadding(new Insets(5));

        HBox FilterRow = BuildWithdrawalDateFilters();
        TableView<WithdrawalRow> WithdrawalTable = CreateWithdrawalTable();
        ObservableList<WithdrawalRow> WithdrawalData = FXCollections.observableArrayList();
        WithdrawalTable.setItems(WithdrawalData);

        Button GetBtn = (Button) FilterRow.getChildren().stream()
            .filter(Node -> Node instanceof Button)
            .findFirst().orElse(null);

        if (GetBtn != null) {
            GetBtn.setOnAction(E -> {
                WithdrawalData.clear();
                WithdrawalData.addAll(GetMockWithdrawalData());
            });
        }

        WithdrawalData.addAll(GetMockWithdrawalData());

        Container.getChildren().addAll(FilterRow, WithdrawalTable);
        VBox.setVgrow(WithdrawalTable, Priority.ALWAYS);

        return Container;
    }

    private HBox BuildWithdrawalDateFilters() {
        HBox FilterRow = new HBox(10);
        FilterRow.setPadding(new Insets(5));
        FilterRow.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;");
        FilterRow.setAlignment(Pos.CENTER_LEFT);

        Label FromLabel = new Label("From Date:");
        DatePicker FromDate = new DatePicker();
        FromDate.setValue(LocalDate.now().minusMonths(3));

        Label ToLabel = new Label("To Date:");
        DatePicker ToDate = new DatePicker();
        ToDate.setValue(LocalDate.now());

        Button GetBtn = new Button("Get");
        GetBtn.setStyle("-fx-font-weight: bold;");

        FilterRow.getChildren().addAll(FromLabel, FromDate, ToLabel, ToDate, GetBtn);
        return FilterRow;
    }

    @SuppressWarnings("unchecked")
    private TableView<WithdrawalRow> CreateWithdrawalTable() {
        TableView<WithdrawalRow> Table = new TableView<>();
        Table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        Table.setStyle("-fx-font-size: 11;");

        Table.getColumns().addAll(
            MakeStringColumn("Requested Date", "RequestedDate", 100),
            MakeDoubleColumn("Amount Requested", "AmountRequested", 110),
            MakeDoubleColumn("Amount Approved", "AmountApproved", 110),
            MakeStringColumn("Withdrawal Mode", "Mode", 120),
            MakeStringColumn("Comments", "Comments", 150),
            MakeStringColumn("Status", "Status", 80)
        );

        return Table;
    }

    private List<WithdrawalRow> GetMockWithdrawalData() {
        return Arrays.asList(
            new WithdrawalRow("2024-04-01", 50000, 50000, "Online Transfer", "Withdrawal request", "Approved"),
            new WithdrawalRow("2024-04-10", 25000, 25000, "Cheque Delivery", "Urgent", "Approved"),
            new WithdrawalRow("2024-04-20", 100000, 0, "Cheque Pickup", "Pending verification", "In-Process"),
            new WithdrawalRow("2024-05-01", 30000, 0, "Online Transfer", "", "Declined")
        );
    }

    private void ShowWithdrawalRequestWindow() {
        Stage RequestStage = new Stage();
        RequestStage.setTitle("Cash Withdrawal Request");
        RequestStage.setResizable(false);

        VBox Root = new VBox(10);
        Root.setPadding(new Insets(15));
        Root.setStyle("-fx-background-color: #F0F0F0;");

        GridPane FormGrid = BuildWithdrawalRequestForm(RequestStage);
        HBox ButtonRow = BuildWithdrawalRequestButtons(RequestStage, FormGrid);

        Root.getChildren().addAll(FormGrid, ButtonRow);
        Scene RequestScene = new Scene(Root, 450, 450);
        RequestStage.setScene(RequestScene);
        RequestStage.show();
    }

    private GridPane BuildWithdrawalRequestForm(Stage RequestStage) {
        GridPane Form = new GridPane();
        Form.setHgap(10);
        Form.setVgap(12);
        Form.setPadding(new Insets(10));
        Form.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC;");

        Form.add(new Label("Account No:"), 0, 0);
        ComboBox<String> AccountCombo = new ComboBox<>();
        AccountCombo.getItems().addAll("123456 - AKD Trading", "789012 - AKD Margin");
        AccountCombo.setValue("123456 - AKD Trading");
        Form.add(AccountCombo, 1, 0);

        Form.add(new Label("PIN:"), 0, 1);
        PasswordField PinField = new PasswordField();
        PinField.setPromptText("Enter PIN");
        Form.add(PinField, 1, 1);

        Form.add(new Label("Available Amount:"), 0, 2);
        TextField AvailableField = new TextField("50,000.00");
        AvailableField.setEditable(false);
        AvailableField.setStyle("-fx-background-color: #EEEEEE;");
        Form.add(AvailableField, 1, 2);

        Form.add(new Label("Withdrawal Amount:"), 0, 3);
        TextField WithdrawalField = new TextField();
        WithdrawalField.setPromptText("0.00");
        Form.add(WithdrawalField, 1, 3);

        Form.add(new Label("Remaining Amount:"), 0, 4);
        TextField RemainingField = new TextField("50,000.00");
        RemainingField.setEditable(false);
        RemainingField.setStyle("-fx-background-color: #EEEEEE;");
        Form.add(RemainingField, 1, 4);

        AttachRemainingAmountListener(WithdrawalField, AvailableField, RemainingField);

        Form.add(new Label("Withdrawal Mode:"), 0, 5);
        Form.add(BuildWithdrawalModeGroup(), 1, 5);

        Form.add(new Label("Comments:"), 0, 6);
        TextArea CommentsArea = new TextArea();
        CommentsArea.setPrefRowCount(3);
        CommentsArea.setPrefWidth(250);
        Form.add(CommentsArea, 1, 6);

        return Form;
    }

    private VBox BuildWithdrawalModeGroup() {
        VBox RadioGroup = new VBox(5);
        ToggleGroup ModeGroup = new ToggleGroup();

        RadioButton ChequeDelivery = new RadioButton("Cheque Delivery");
        RadioButton ChequePickup = new RadioButton("Cheque Pickup");
        RadioButton OnlineTransfer = new RadioButton("Online Fund Transfer");

        ChequeDelivery.setToggleGroup(ModeGroup);
        ChequePickup.setToggleGroup(ModeGroup);
        OnlineTransfer.setToggleGroup(ModeGroup);
        OnlineTransfer.setSelected(true);

        RadioGroup.getChildren().addAll(ChequeDelivery, ChequePickup, OnlineTransfer);
        return RadioGroup;
    }

    private void AttachRemainingAmountListener(TextField WithdrawalField, TextField AvailableField, TextField RemainingField) {
        WithdrawalField.textProperty().addListener((Obs, OldVal, NewVal) -> {
            try {
                double Available = Double.parseDouble(AvailableField.getText().replace(",", ""));
                double Withdraw = NewVal.isEmpty() ? 0 : Double.parseDouble(NewVal);
                double Remaining = Available - Withdraw;
                RemainingField.setText(String.format("%,.2f", Math.max(Remaining, 0)));
            } catch (NumberFormatException Ex) {
                RemainingField.setText("Invalid amount");
            }
        });
    }

    private HBox BuildWithdrawalRequestButtons(Stage RequestStage, GridPane FormGrid) {
        HBox ButtonRow = new HBox(10);
        ButtonRow.setAlignment(Pos.CENTER);

        Button SubmitBtn = new Button("Submit Request");
        SubmitBtn.setStyle("-fx-background-color: #3366CC; -fx-text-fill: white; -fx-font-weight: bold;");
        SubmitBtn.setOnAction(E -> HandleWithdrawalSubmit(RequestStage, FormGrid));

        Button CancelBtn = new Button("Cancel");
        CancelBtn.setOnAction(E -> RequestStage.close());

        ButtonRow.getChildren().addAll(SubmitBtn, CancelBtn);
        return ButtonRow;
    }

    private void HandleWithdrawalSubmit(Stage RequestStage, GridPane FormGrid) {
        PasswordField PinField = (PasswordField) FormGrid.getChildren().stream()
            .filter(Node -> Node instanceof PasswordField)
            .findFirst().orElse(null);

        TextField WithdrawalField = null;
        int TextFieldCount = 0;
        for (javafx.scene.Node Node : FormGrid.getChildren()) {
            if (Node instanceof TextField && ((TextField) Node).isEditable()) {
                TextFieldCount++;
                if (TextFieldCount == 1) {
                    WithdrawalField = (TextField) Node;
                }
            }
        }

        ToggleGroup ModeGroup = null;
        for (javafx.scene.Node Node : FormGrid.getChildren()) {
            if (Node instanceof VBox) {
                VBox ModeBox = (VBox) Node;
                if (!ModeBox.getChildren().isEmpty() && ModeBox.getChildren().get(0) instanceof RadioButton) {
                    ModeGroup = ((RadioButton) ModeBox.getChildren().get(0)).getToggleGroup();
                    break;
                }
            }
        }

        if (PinField != null && PinField.getText().isEmpty()) {
            ShowErrorAlert("PIN is required");
            return;
        }

        if (WithdrawalField == null || WithdrawalField.getText().isEmpty()) {
            ShowErrorAlert("Valid withdrawal amount required");
            return;
        }

        try {
            double Amount = Double.parseDouble(WithdrawalField.getText());
            if (Amount <= 0) {
                ShowErrorAlert("Valid withdrawal amount required");
                return;
            }
        } catch (NumberFormatException Ex) {
            ShowErrorAlert("Valid withdrawal amount required");
            return;
        }

        String SelectedMode = "Online Fund Transfer";
        if (ModeGroup != null && ModeGroup.getSelectedToggle() != null) {
            SelectedMode = ((RadioButton) ModeGroup.getSelectedToggle()).getText();
        }

        Alert Success = new Alert(Alert.AlertType.INFORMATION);
        Success.setTitle("Request Submitted");
        Success.setHeaderText(null);
        Success.setContentText("Withdrawal request of Rs. " + WithdrawalField.getText() + " submitted via " + SelectedMode);
        Success.showAndWait();
        RequestStage.close();
    }

    private void ShowErrorAlert(String Message) {
        Alert ErrorAlert = new Alert(Alert.AlertType.ERROR);
        ErrorAlert.setContentText(Message);
        ErrorAlert.show();
    }

    private void RestoreDefaultLayout() {
        SplitPane MainSplit = (SplitPane) RootPane.getCenter();
        if (MainSplit != null && !MainSplit.getDividers().isEmpty()) {
            MainSplit.setDividerPositions(0.7);
        }
        MainTabs.getSelectionModel().select(0);
        if (ProfileTabs != null) {
            ProfileTabs.getSelectionModel().select(0);
        }

        Alert LayoutAlert = new Alert(Alert.AlertType.INFORMATION);
        LayoutAlert.setTitle("Layout Reset");
        LayoutAlert.setHeaderText(null);
        LayoutAlert.setContentText("Default layout restored");
        LayoutAlert.showAndWait();
    }

    private VBox BuildMarketDepthView() {
        VBox Container = new VBox(5);
        Container.setPadding(new Insets(5));
        Container.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;");

        Label Title = new Label("Market Depth");
        Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox TablesBox = new HBox(10);
        TablesBox.getChildren().addAll(
            BuildDepthTable("By Order"),
            BuildDepthTable("By Price")
        );

        Container.getChildren().addAll(Title, TablesBox);
        return Container;
    }

    @SuppressWarnings("unchecked")
    private VBox BuildDepthTable(String TableTitle) {
        VBox Box = new VBox(3);

        Label TitleLabel = new Label(TableTitle);
        TitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        TableView<DepthRow> Table = new TableView<>();
        Table.setPrefWidth(200);
        Table.setStyle("-fx-font-size: 10;");

        Table.getColumns().addAll(
            MakeIntColumn("Volume", "Volume", 80),
            MakeDoubleColumn("Price", "Price", 80),
            MakeStringColumn("Flag", "Flag", 40)
        );

        Table.setItems(GetMockDepthData());
        Box.getChildren().addAll(TitleLabel, Table);
        return Box;
    }

    private ObservableList<DepthRow> GetMockDepthData() {
        return FXCollections.observableArrayList(
            new DepthRow(500, 112.50, "B"),
            new DepthRow(1000, 112.45, "B"),
            new DepthRow(250, 112.40, "B"),
            new DepthRow(300, 112.55, "S"),
            new DepthRow(800, 112.60, "S")
        );
    }

    private VBox BuildMessageCenter() {
        VBox Container = new VBox(5);
        Container.setPadding(new Insets(5));
        Container.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;");

        Label Title = new Label("Messages");
        Title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TabPane MessageTabs = new TabPane();
        MessageTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        MessageTabs.setPrefHeight(200);

        String[] TabNames = {"Activity", "Orders", "Announcements", "News"};
        String[][] Messages = {
            {"09:45 - Market opened", "10:00 - Order placed for ENGRO", "10:30 - Position squared off"},
            {"09:50 - Buy 500 ENGRO @ 112.50", "10:15 - Sell 200 OGDC @ 125.25"},
            {"10:00 - Dividend declared for NBP", "11:00 - Board meeting on Friday"},
            {"09:30 - PSX benchmark index up 300 points", "10:20 - Oil prices stabilize"}
        };

        for (int I = 0; I < TabNames.length; I++) {
            MessageTabs.getTabs().add(BuildMessageTab(TabNames[I], Messages[I]));
        }

        Container.getChildren().addAll(Title, MessageTabs);
        return Container;
    }

    private Tab BuildMessageTab(String Name, String[] Messages) {
        VBox MsgBox = new VBox(5);
        MsgBox.setPadding(new Insets(5));

        ListView<String> MessageList = new ListView<>();
        MessageList.getItems().addAll(Messages);
        MsgBox.getChildren().add(MessageList);

        return new Tab(Name, MsgBox);
    }

    private HBox BuildStatusBar() {
        HBox StatusBar = new HBox(15);
        StatusBar.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 4 10 4 10;");
        StatusBar.setAlignment(Pos.CENTER_LEFT);

        Label DateLabel = new Label("Date: " + LocalDate.now().toString());
        DateLabel.setFont(Font.font("Arial", 11));

        Label MarketStatus = new Label("Market Status: Open");
        MarketStatus.setFont(Font.font("Arial", 11));
        MarketStatus.setTextFill(Color.web("#009900"));

        Region Spacer = new Region();
        HBox.setHgrow(Spacer, Priority.ALWAYS);

        Label RecordCount = new Label("Records: 16");
        RecordCount.setFont(Font.font("Arial", 11));

        StatusBar.getChildren().addAll(DateLabel, MarketStatus, Spacer, RecordCount);
        return StatusBar;
    }

    private VBox BuildPlaceholderPane(String Name) {
        VBox Placeholder = new VBox();
        Placeholder.setAlignment(Pos.CENTER);
        Label Lbl = new Label(Name + " - Content Area");
        Lbl.setFont(Font.font("Arial", 14));
        Lbl.setTextFill(Color.GRAY);
        Placeholder.getChildren().add(Lbl);
        return Placeholder;
    }

    public static class MarketRow {
        private final SimpleStringProperty Mkt;
        private final SimpleStringProperty Scrip;
        private final SimpleDoubleProperty LastPrice;
        private final SimpleDoubleProperty Change;
        private final SimpleDoubleProperty Buy;
        private final SimpleIntegerProperty BVol;
        private final SimpleDoubleProperty Sell;
        private final SimpleIntegerProperty SVol;
        private final SimpleDoubleProperty High;
        private final SimpleDoubleProperty Low;
        private final SimpleIntegerProperty TotalVolume;
        private final SimpleDoubleProperty Average;
        private final SimpleDoubleProperty ClosePrice;

        public MarketRow(String Mkt, String Scrip, double LastPrice, double Change,
                         double Buy, int BVol, double Sell, int SVol,
                         double High, double Low, int TotalVolume, double Average, double ClosePrice) {
            this.Mkt = new SimpleStringProperty(Mkt);
            this.Scrip = new SimpleStringProperty(Scrip);
            this.LastPrice = new SimpleDoubleProperty(LastPrice);
            this.Change = new SimpleDoubleProperty(Change);
            this.Buy = new SimpleDoubleProperty(Buy);
            this.BVol = new SimpleIntegerProperty(BVol);
            this.Sell = new SimpleDoubleProperty(Sell);
            this.SVol = new SimpleIntegerProperty(SVol);
            this.High = new SimpleDoubleProperty(High);
            this.Low = new SimpleDoubleProperty(Low);
            this.TotalVolume = new SimpleIntegerProperty(TotalVolume);
            this.Average = new SimpleDoubleProperty(Average);
            this.ClosePrice = new SimpleDoubleProperty(ClosePrice);
        }

        public String getMkt() { return Mkt.get(); }
        public String getScrip() { return Scrip.get(); }
        public double getLastPrice() { return LastPrice.get(); }
        public double getChange() { return Change.get(); }
        public double getBuy() { return Buy.get(); }
        public int getBVol() { return BVol.get(); }
        public double getSell() { return Sell.get(); }
        public int getSVol() { return SVol.get(); }
        public double getHigh() { return High.get(); }
        public double getLow() { return Low.get(); }
        public int getTotalVolume() { return TotalVolume.get(); }
        public double getAverage() { return Average.get(); }
        public double getClosePrice() { return ClosePrice.get(); }
    }

    public static class PortfolioRow {
        private final SimpleStringProperty Mkt;
        private final SimpleStringProperty Scrip;
        private final SimpleIntegerProperty NetQty;
        private final SimpleDoubleProperty AvgPrice;
        private final SimpleDoubleProperty Change;
        private final SimpleDoubleProperty Average;
        private final SimpleIntegerProperty BVol;
        private final SimpleDoubleProperty Buy;
        private final SimpleDoubleProperty Sell;
        private final SimpleIntegerProperty SVol;
        private final SimpleDoubleProperty LastPrice;
        private final SimpleIntegerProperty TotalVolume;
        private final SimpleDoubleProperty High;
        private final SimpleDoubleProperty Low;
        private final SimpleDoubleProperty ClosePrice;
        private final SimpleStringProperty State;

        public PortfolioRow(String Mkt, String Scrip, int NetQty, double AvgPrice, double Change,
                            double Average, int BVol, double Buy, double Sell, int SVol,
                            double LastPrice, int TotalVolume, double High, double Low,
                            double ClosePrice, String State) {
            this.Mkt = new SimpleStringProperty(Mkt);
            this.Scrip = new SimpleStringProperty(Scrip);
            this.NetQty = new SimpleIntegerProperty(NetQty);
            this.AvgPrice = new SimpleDoubleProperty(AvgPrice);
            this.Change = new SimpleDoubleProperty(Change);
            this.Average = new SimpleDoubleProperty(Average);
            this.BVol = new SimpleIntegerProperty(BVol);
            this.Buy = new SimpleDoubleProperty(Buy);
            this.Sell = new SimpleDoubleProperty(Sell);
            this.SVol = new SimpleIntegerProperty(SVol);
            this.LastPrice = new SimpleDoubleProperty(LastPrice);
            this.TotalVolume = new SimpleIntegerProperty(TotalVolume);
            this.High = new SimpleDoubleProperty(High);
            this.Low = new SimpleDoubleProperty(Low);
            this.ClosePrice = new SimpleDoubleProperty(ClosePrice);
            this.State = new SimpleStringProperty(State);
        }

        public String getMkt() { return Mkt.get(); }
        public String getScrip() { return Scrip.get(); }
        public int getNetQty() { return NetQty.get(); }
        public double getAvgPrice() { return AvgPrice.get(); }
        public double getChange() { return Change.get(); }
        public double getAverage() { return Average.get(); }
        public int getBVol() { return BVol.get(); }
        public double getBuy() { return Buy.get(); }
        public double getSell() { return Sell.get(); }
        public int getSVol() { return SVol.get(); }
        public double getLastPrice() { return LastPrice.get(); }
        public int getTotalVolume() { return TotalVolume.get(); }
        public double getHigh() { return High.get(); }
        public double getLow() { return Low.get(); }
        public double getClosePrice() { return ClosePrice.get(); }
        public String getState() { return State.get(); }
    }

    public static class OrderHistoryRow {
        private final SimpleStringProperty Scrip;
        private final SimpleIntegerProperty Quantity;
        private final SimpleDoubleProperty GrossRate;
        private final SimpleDoubleProperty NetAmount;
        private final SimpleStringProperty ClearingDate;

        public OrderHistoryRow(String Scrip, int Qty, double Rate, double Amount, String Date) {
            this.Scrip = new SimpleStringProperty(Scrip);
            this.Quantity = new SimpleIntegerProperty(Qty);
            this.GrossRate = new SimpleDoubleProperty(Rate);
            this.NetAmount = new SimpleDoubleProperty(Amount);
            this.ClearingDate = new SimpleStringProperty(Date);
        }

        public String getScrip() { return Scrip.get(); }
        public int getQuantity() { return Quantity.get(); }
        public double getGrossRate() { return GrossRate.get(); }
        public double getNetAmount() { return NetAmount.get(); }
        public String getClearingDate() { return ClearingDate.get(); }
    }

    public static class WithdrawalRow {
        private final SimpleStringProperty RequestedDate;
        private final SimpleDoubleProperty AmountRequested;
        private final SimpleDoubleProperty AmountApproved;
        private final SimpleStringProperty Mode;
        private final SimpleStringProperty Comments;
        private final SimpleStringProperty Status;

        public WithdrawalRow(String Date, double Req, double App, String Mode, String Comments, String Status) {
            this.RequestedDate = new SimpleStringProperty(Date);
            this.AmountRequested = new SimpleDoubleProperty(Req);
            this.AmountApproved = new SimpleDoubleProperty(App);
            this.Mode = new SimpleStringProperty(Mode);
            this.Comments = new SimpleStringProperty(Comments);
            this.Status = new SimpleStringProperty(Status);
        }

        public String getRequestedDate() { return RequestedDate.get(); }
        public double getAmountRequested() { return AmountRequested.get(); }
        public double getAmountApproved() { return AmountApproved.get(); }
        public String getMode() { return Mode.get(); }
        public String getComments() { return Comments.get(); }
        public String getStatus() { return Status.get(); }
    }

    public static class DepthRow {
        private final SimpleIntegerProperty Volume;
        private final SimpleDoubleProperty Price;
        private final SimpleStringProperty Flag;

        public DepthRow(int Vol, double Price, String Flag) {
            this.Volume = new SimpleIntegerProperty(Vol);
            this.Price = new SimpleDoubleProperty(Price);
            this.Flag = new SimpleStringProperty(Flag);
        }

        public int getVolume() { return Volume.get(); }
        public double getPrice() { return Price.get(); }
        public String getFlag() { return Flag.get(); }
    }

    public static void main(String[] Args) {
        launch(Args);
    }
}