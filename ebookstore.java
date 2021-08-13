//
import java.util.*;
import java.sql.*;

public class ebookstore {
    public static void main(String[] args){
        //Create table connection and statement
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebookstore?useSSL=false","root","XXXXX");

            Statement statement = connection.createStatement();
        ){
            Scanner input = new Scanner(System.in);
            int selection = 0;

            //Menu
                System.out.println("Please select a number from the menu below:\n "
                        + "1. Enter book\n" + "2. Update book\n" + "3. Delete book\n" + "4. Search books\n" + "0. Exit");
                selection = input.nextInt();
                //Actions for the menu selection according to user input
            while(selection != 0){

                if(selection == 1){
                    input.nextLine();
                    System.out.print("Please enter the title of the book you would like to add to the database: ");
                    String title = input.nextLine();
                    System.out.print("Please enter the author of the book: ");
                    String author = input.nextLine();
                    System.out.print("Please enter the number of books in stock: ");
                    int qty = input.nextInt();
                    addBook(title, author,qty,statement);

                    System.out.println("Please select a number from the menu below:\n "
                            + "1. Enter book\n" + "2. Update book\n" + "3. Delete book\n" + "4. Search books\n" + "0. Exit");
                    selection = input.nextInt();

                }
                //Update a book
                else if(selection == 2){
                    System.out.println("Please enter the id of the book you would like to update: ");
                    int id = input.nextInt();
                    updateBook(id,statement,input);

                    System.out.println("Please select a number from the menu below:\n "
                            + "1. Enter book\n" + "2. Update book\n" + "3. Delete book\n" + "4. Search books\n" + "0. Exit");
                    selection = input.nextInt();
                    //break;
                }
                //Delete book from the database
                else if(selection == 3){
                    System.out.print("Please enter the id of the book you would like to delete: ");
                    int id = input.nextInt();

                    deleteBook(id,statement);
                    break;
                }//Search for a book in the database
                else if(selection == 4){
                    searchStore(statement,input);
                }
                //Quit the program
                else if(selection == 0){
                    System.out.println("Goodbye");
                    input.close();
                }
                else{
                    System.out.println("Invalid selection");
                }
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    //SQL command to add a new book
    public static void addBook(String title, String author, int qty, Statement statement){
        try{
            String strMaxId = "Select max(id) from books";
            ResultSet result = statement.executeQuery(strMaxId);
            result.next();
            int id = result.getInt(1) + 1;

            title = '"' + title + '"';
            author = '"' + author + '"';

            String strInsert = "insert into books " + "values(" + id + ", " + title + ", " + author + ", " + qty + ")";
            int countInserted = statement.executeUpdate(strInsert);
            System.out.println(countInserted + " book has been added.\n");
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

    }//SQL command to update an existing book in the database
    public static void updateBook(int id, Statement statement, Scanner input) {
        try{
            String strDisplay = "select * from books where id = " + id;
            ResultSet resultSet = statement.executeQuery(strDisplay);
            resultSet.next();
            String rTitle = resultSet.getString("Title");
            String rAuthor = resultSet.getString("Author");
            int rQty = resultSet.getInt("Qty");
            System.out.println("Here is the current information of the selected book: ");
            System.out.println("Title: " + rTitle + "\n" + "Author: " + rAuthor + "\n" + "Quantity: " + rQty + "\n");

            int selection = 0;
//Menu for the field to be updated
            while(selection != 4){
                System.out.println("Please enter the number of the field you would like to update: \n" + "1. Title\n" + "2. Author\n" + "3. Quantity\n" + "4. Return");
                selection = input.nextInt();
//Update title
                if(selection == 1){
                    input.nextLine();
                    System.out.print("Please enter the new title: ");
                    String newTitle = "'" + input.nextLine() + "'";

                    String strUpdate = "update books set Title = " + newTitle + " " + "where id = " + id;
                    int countUpdate = statement.executeUpdate(strUpdate);
                    System.out.print(countUpdate + "book has been updated.\n");
                }//Update author
                else if(selection == 2){
                    input.nextLine();
                    System.out.print(" Please enter the new author: ");
                    String newAuthor = "'" + input.nextLine() + "'";

                    String strUpdate = "update books set Author = " + newAuthor + " " + "where id = " + id;
                    int countUpdate = statement.executeUpdate(strUpdate);
                    System.out.println(countUpdate + " author has been updated.\n");
                }//Update quantity
                else if(selection == 3){
                    System.out.print("Please enter the updated quantity: ");
                    int newQty = input.nextInt();

                    String strUpdate = "update books set Qty = " + newQty + " " + "where id = " + id;
                    int countUpdate = statement.executeUpdate(strUpdate);
                    System.out.println(countUpdate + " Quantity has been updated.\n");
                }
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }//SQL command for deleting a book
    public static void deleteBook(int id, Statement statement){
        try{
            String strDelete = "delete from books " + "where id = " + id;

            int countDeleted = statement.executeUpdate(strDelete);
            if(countDeleted == 1){
                System.out.println(countDeleted + " book has been deleted.\n");
            }
            else{
                System.out.println("No books have been deleted.\n");
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }//SQL command to search the store
    public static void searchStore(Statement statement, Scanner input){
        try{
            int selection = 0;
            while(selection != 5){
                System.out.println("Which field would you like to search?:\n" + "1. ID\n" + "2. Title\n" + "3. Author\n" + "4. Quantity\n" + "5. Return");
                selection = input.nextInt();

                if(selection == 1){
                    System.out.print("Please enter the ID of the book: ");
                    int bookData = input.nextInt();

                    String strSelect = "select * from books where id = " + bookData;
                    ResultSet resultSet = statement.executeQuery(strSelect);

                    System.out.println("\nHere is the the book with the entered ID: ");
                    resultSetPrinter(resultSet);
                }
                else if(selection == 2){
                    input.nextLine();
                    System.out.print("Please enter a full or partial title of the book: ");
                    String bookData = input.nextLine();

                    String strSelect = "select * from books where Title like '%' " + bookData + "'%'";
                    ResultSet resultSet = statement.executeQuery(strSelect);

                    System.out.println("\nHere are the books that have the entered title: ");
                    resultSetPrinter(resultSet);
                }
                else if(selection == 3){
                    input.nextLine();
                    System.out.print("Please enter a full or partial author name for a list of their books: ");
                    String bookData = input.nextLine();

                    String strSelect = "select * from books where Author like '%'" + bookData + "'%'";
                    ResultSet resultSet = statement.executeQuery(strSelect);
                    //Searches database for the Author
                    System.out.println("\nHere are the boooks that were written by authors from your entry: ");
                    resultSetPrinter(resultSet);
                }
                else if(selection == 4){
                    System.out.print("Please enter the quantity of books you would like to search: ");
                    int bookData = input.nextInt();
                    //Searches database for the given quantity
                    String strSelect = "select * from books where Qty <= " + bookData;
                    ResultSet resultSet = statement.executeQuery(strSelect);
                    System.out.println("\nHere are the books that have the quantity entered and below: ");
                    resultSetPrinter(resultSet);
                }
                else if(selection == 5){
                    System.out.println("Returning to main menu.\n");
                }
                else{
                    System.out.println("Invalid selection");
                }
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    public static void resultSetPrinter(ResultSet resultSet){
        try {
            if (resultSet.next()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("Title");
                    String author = resultSet.getString("Author");
                    int qty = resultSet.getInt("Qty");
                    System.out.println("ID: " + id + "\nTitle" + title + "\nAuthor: " + author + "\nQuantity: " + qty + "\n");
                }
            }
            else{
                System.out.println("No records for the entry could be found in the database.\n");
            }
        }
            catch(SQLException ex){
                ex.printStackTrace();

            }
        }
    }

