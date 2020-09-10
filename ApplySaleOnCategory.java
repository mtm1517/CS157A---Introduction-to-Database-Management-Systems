
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

class ApplySaleOnCategory
{
    public static Pane getApplySaleOnCatPane()
    {
    	
    	System.out.print("heee111111e");
        //choose category pair of fields
        HBox catPairPane = new HBox(10);
        catPairPane.setAlignment(Pos.CENTER_LEFT);
        catPairPane.getChildren().addAll(new Label("Choose category to apply discount on: "), categories);
        categories.setValue(categories.getItems().get(0));

        //disount pair of fields
        HBox discountPairPane = new HBox(10);
        discountPairPane.setAlignment(Pos.CENTER_LEFT);
        discountPairPane.getChildren().addAll(new Label("Enter the amount of discount percentage wise: "), discountField);
        //Note for assistance with the number inputed.
        Label discountAmount = new Label("For example: If you want to apply 50% discount, input 0.50 .");
        //main pane
        VBox mainPane = new VBox(10);
        mainPane.setAlignment(Pos.TOP_LEFT);
        mainPane.setPadding(new Insets(10,25,10,25));
        mainPane.getChildren().addAll(backToSalemenuBtn ,catPairPane, discountPairPane, discountAmount, submit);
        
        // add action handlers
        backToSalemenuBtn.setOnAction(MainMenu.backtoSalesMenu());
        submit.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent event)
            {
            	//System.out.print("heee22222222e");
            	Connection con = ConnectionFactory.getConnection();
                try 
                {
                	//System.out.print("heee333333e");
                	double discount = Double.parseDouble(discountField.getText());
                    if(discount > 1 || discount <= 0) throw new NumberFormatException();
                    String querytext = 
                    "CALL apply_sale_on_category("+ categories.getValue().id + ","+ discountField.getText() +")";
                    PreparedStatement query = con.prepareStatement(querytext);
                    query.execute();
                    mainPane.getChildren().addAll(new Label ("The Discount have been submited"));
                    //close the connection
                    con.close();
                } 
                catch (SQLException e) 
                {
                	
                	System.err.println("problem with executing the query");
                    e.printStackTrace();
                }
                catch(NumberFormatException e)
                {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Discount format is incorrect.");
                    alert.setHeaderText(null);
                    alert.setContentText("The discount format should be a decimal number bigger that 0 and less that 1");
                    alert.showAndWait();
                }

            }
        });


        return mainPane;

    }
    private static ObservableList<Category> populateWithCategories()
    {
        ObservableList<Category> data = FXCollections.observableArrayList();
        Connection con = ConnectionFactory.getConnection();
        try 
        {
            PreparedStatement query = con.prepareStatement("Select * From category;");
            if(query.execute())
            {
                ResultSet results = query.getResultSet();
                while(results.next())
                {
                    data.add(new Category(results.getInt(1), results.getString(2)));
                }
                //close the connecetion
                con.close();
            }
            
        } catch (SQLException e) 
        {
            System.out.println("There is a problem in populating the combo box: ");
            e.printStackTrace();
        }

        return data;
    }

    private static TextField discountField = new TextField();
    private static ComboBox<Category> categories = new ComboBox<>(populateWithCategories());
    private static Button   submit = new Button("submit discount"),
                            backToSalemenuBtn = new Button("<-- Back");
}

class Category
{
    public int id;
    public String name;
    public Category(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }
}