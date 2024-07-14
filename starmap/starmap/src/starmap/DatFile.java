package starmap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.*;

public class DatFile {
    List<Star> stars = new ArrayList<>();

    public DatFile() {
        String url = "jdbc:mysql://localhost:3306/stars500";
        String username = "root";
        String password = "1104";
        String tableName = "star";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT * FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String sql = "SELECT COUNT(*) AS row_count FROM star" ;
            int rowCount=0;
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    rowCount = rs.getInt("row_count");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            Object[][] rowsArray = new Object[rowCount][columnCount];

            int cnt_row=0;
            while (resultSet.next()) {
                Object[] values = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    values[i - 1] = resultSet.getObject(i);
                }
                rowsArray[cnt_row++] = values;

            }

            resultSet.close();
            statement.close();
            connection.close();

            for (int i = 0; i < rowCount; i++) {
                if(rowsArray[i][0]==null||rowsArray[i][7]==null||rowsArray[i][6]==null) {
                    continue;
                }
                int ss = (int)rowsArray[i][0];
                double x = 1/Math.tan((double)rowsArray[i][7]);
                double y = x*Math.sin((double)rowsArray[i][6]);
                x=x*Math.cos((double)rowsArray[i][6]);
                x=x*1920;
                y=y*1080;
                String Ns = "";
                if(rowsArray[i][1]!=null) {
                    Ns = (String)rowsArray[i][1];}
                double cs=0;
                if(rowsArray[i][5]!=null) {
                    cs = (double)rowsArray[i][5];}
                double mag= 0;
                if(rowsArray[i][0]!=null) {
                    mag = ((double)rowsArray[i][4])/17.34;
                    mag=mag*5;
                }
                if(!Ns.equals("")) {
                    mag=mag+8;
                }
                BigDecimal dx = BigDecimal.valueOf(0);
                if(rowsArray[i][2]!=null) {
                    dx = (BigDecimal)rowsArray[i][2];
                }
                int xs =(int) Math.round(x);
                int ys = (int)Math.round(y);
                int mags = (int)Math.round(mag);
                stars.add(new Star(ss,Ns,xs,ys,mags,cs,dx));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unused")
	public static void main(String args[]) {
        DatFile k = new DatFile();
    }
}

