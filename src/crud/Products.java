/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package crud;

import connection.MyDatabase;
import java.awt.Component;
import java.awt.List;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author lid2jvl
 */
public class Products extends javax.swing.JDialog {

    /**
     * Creates new form Products
     */
    public Products(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    
        setResizable(false);
        setLocationRelativeTo(null);
        addCategory();
        searchProduct();
        initTableListenerProduct();
    }
    
    MyDatabase db = new MyDatabase();
    
    private void addCategory(){
        if(db.getConnection()){
            try{
                String query = "SELECT id_category, name_category FROM category";
                PreparedStatement smtp = db.conn.prepareStatement(query);
                ResultSet result = smtp.executeQuery();
                
                while(result.next()){
                    jCcategory.addItem(result.getString("name_category"));
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Error to get category datas!"+e.toString());
            }
        }
    }
    
    private String searchIdCategory(){
        if(db.getConnection()){
            
            try{
                String selectedValue = jCcategory.getSelectedItem().toString();

                String queryCategory = "select id_category, name_category from category";
                PreparedStatement smtpCategory = db.conn.prepareStatement(queryCategory);
                ResultSet resultCategory = smtpCategory.executeQuery();

                String id_category = "1";
                while(resultCategory.next()){
                    System.out.println(resultCategory.getString("name_category"));
                    System.out.println(selectedValue);
                    if(resultCategory.getString("name_category").equals(selectedValue)){
                        System.out.println(resultCategory.getString("id_category"));
                        id_category = resultCategory.getString("id_category");
                        break;
                    }
                }

                return id_category;
            }catch(SQLException error){
                JOptionPane.showMessageDialog(null, "Insert error in the database!"+error.toString());
                return "";
            }
        }
        return "";
    }
    
    private void registerCustumer(){
        if(db.getConnection()){
            try{
                
                String id_category = searchIdCategory();
                
                String query = "insert product (name_product, value_product, mark_product, description_product, id_category) values(?,?,?,?,?)";
                PreparedStatement smtp = db.conn.prepareStatement(query);
                smtp.setString(1, jTname.getText());
                smtp.setString(2, jTvalue.getText());
                smtp.setString(3, jTmark.getText());
                smtp.setString(4, jTdescription.getText());
                
                smtp.setString(5, id_category);
                
                smtp.executeUpdate();
                JOptionPane.showMessageDialog(null, "DATAS WAS REGISTERED");
                smtp.close();
                db.conn.close();
            }catch(SQLException error){
                JOptionPane.showMessageDialog(null, "Insert error in the database!"+error.toString());
            }
        }
    }
    
    private void searchProduct(){
        if(db.getConnection()){
            try{
                String query = "select * from product where name_product like ?";
                PreparedStatement smtp = db.conn.prepareStatement(query);
                smtp.setString(1,"%"+jTsearch.getText()+"%");
                ResultSet rs = smtp.executeQuery();
                DefaultTableModel table = (DefaultTableModel) jTableProduct.getModel();
                table.setNumRows(0);
                while(rs.next()){
                    table.addRow(new Object[] {
                        rs.getString("id_product"),
                        rs.getString("name_product"),
                        rs.getString("value_product"),
                        rs.getString("mark_product"),
                        rs.getString("description_product"),
                        rs.getString("id_category"),
                    });                                   
                }
                smtp.close();
                db.conn.close();
            }catch(SQLException e){
                System.out.println("Error to searching "+e);
            }
        }
    }
    
    private void initTableListenerProduct(){
        jTableProduct.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int selectedRow = jTableProduct.getSelectedRow();
                if(selectedRow != -1){
                    jLidProduct.setText(jTableProduct.getValueAt(selectedRow, 0).toString());
                    jTname.setText(jTableProduct.getValueAt(selectedRow, 1).toString());
                    jTvalue.setText(jTableProduct.getValueAt(selectedRow, 2).toString());
                    jTmark.setText(jTableProduct.getValueAt(selectedRow, 3).toString());
                    jTdescription.setText(jTableProduct.getValueAt(selectedRow, 4).toString());
                    jCcategory.setSelectedItem(jTableProduct.getValueAt(selectedRow, 5).toString());
                }
            }
        });
    }
    
    private void changeProductDatas(){
        if(db.getConnection()){
            try{
                String id_category = searchIdCategory();
                
                String query = "update product set name_product=?, value_product=?, mark_product=?, "+
                                "description_product=?, id_category=? where id_product = ? ";
                
                PreparedStatement changeDatas = db.conn.prepareStatement(query);
                changeDatas.setString(1, jTname.getText());
                changeDatas.setString(2, jTvalue.getText());
                changeDatas.setString(3, jTmark.getText());
                changeDatas.setString(4, jTdescription.getText());
                changeDatas.setString(5, id_category);
                changeDatas.setString(6, jLidProduct.getText());
                
                changeDatas.executeUpdate();
                JOptionPane.showMessageDialog(null, "CHANDED DATAS!");
                
                changeDatas.close();
                db.conn.close();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "ERROR IN THE SQL"+e.getMessage());
            }
        }
    }
    
    private void remove_costumer(){
        if(db.getConnection()){
            try{
                String query = "DELETE FROM product WHERE id_product = ?";
                PreparedStatement remove = db.conn.prepareStatement(query);
                String index = (String)jTableProduct.getModel().getValueAt(jTableProduct.getSelectedRow(), 0);
                remove.setString(1, index);
                
                int choice = JOptionPane.showConfirmDialog(null, "You want to remove product?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if(choice == JOptionPane.YES_OPTION){
                    int result = remove.executeUpdate();
                    if(result>0){
                        JOptionPane.showMessageDialog(null, "Product removed with success!");
                    }else{
                        JOptionPane.showMessageDialog(null, "Was cannot to remve the Product!");
                    }
                    
                    remove.close();
                    db.conn.close();
                }
                
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Error to remove"+e.toString());
            }
        }
    }
    
    private void clearFields(JPanel jPanel){
        Component[] components = jPanel.getComponents();
        jTdescription.setText("");
        for(Component component : components){
            if(component instanceof JTextField){
                JTextField fieldsTF = (JTextField)component;
                fieldsTF.setText("");
            }
            
            
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jBadd = new javax.swing.JButton();
        jBremove = new javax.swing.JButton();
        jBedit = new javax.swing.JButton();
        jBclear = new javax.swing.JButton();
        jclose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLidProduct = new javax.swing.JLabel();
        jPanelDataProduct1 = new javax.swing.JPanel();
        jTname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTmark = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTvalue = new javax.swing.JTextField();
        jPanelDataProduct2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTdescription = new javax.swing.JTextArea();
        jCcategory = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProduct = new javax.swing.JTable();
        jBadd1 = new javax.swing.JButton();
        jBremove1 = new javax.swing.JButton();
        jBedit1 = new javax.swing.JButton();
        jBclear1 = new javax.swing.JButton();
        jclose1 = new javax.swing.JButton();
        jTsearch = new javax.swing.JTextField();
        jBsearch = new javax.swing.JButton();

        jBadd.setBackground(new java.awt.Color(48, 56, 72));
        jBadd.setForeground(new java.awt.Color(255, 255, 255));
        jBadd.setText("Add");
        jBadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBaddActionPerformed(evt);
            }
        });

        jBremove.setBackground(new java.awt.Color(48, 56, 72));
        jBremove.setForeground(new java.awt.Color(255, 255, 255));
        jBremove.setText("Remove");
        jBremove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBremoveActionPerformed(evt);
            }
        });

        jBedit.setBackground(new java.awt.Color(48, 56, 72));
        jBedit.setForeground(new java.awt.Color(255, 255, 255));
        jBedit.setText("Edit");
        jBedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBeditActionPerformed(evt);
            }
        });

        jBclear.setBackground(new java.awt.Color(48, 56, 72));
        jBclear.setForeground(new java.awt.Color(255, 255, 255));
        jBclear.setText("Clear");
        jBclear.setBorderPainted(false);

        jclose.setText("Close");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(250, 250, 250));

        jPanel1.setForeground(new java.awt.Color(250, 250, 250));

        jPanel2.setBackground(new java.awt.Color(48, 56, 72));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("REGISTER OF PRODUCTS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel14.setBackground(new java.awt.Color(0, 73, 204));

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Fields of Product");

        jLidProduct.setText("ID");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLidProduct)
                .addGap(34, 34, 34))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLidProduct))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelDataProduct1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(187, 187, 187)));

        jTname.setToolTipText("");
        jTname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTnameActionPerformed(evt);
            }
        });

        jLabel2.setText("Name of Product");

        jTmark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTmarkActionPerformed(evt);
            }
        });

        jLabel3.setText("Value of product");

        jLabel4.setText("Mark of product");

        javax.swing.GroupLayout jPanelDataProduct1Layout = new javax.swing.GroupLayout(jPanelDataProduct1);
        jPanelDataProduct1.setLayout(jPanelDataProduct1Layout);
        jPanelDataProduct1Layout.setHorizontalGroup(
            jPanelDataProduct1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataProduct1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanelDataProduct1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jTmark, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jTname)
                    .addComponent(jTvalue))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanelDataProduct1Layout.setVerticalGroup(
            jPanelDataProduct1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataProduct1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTvalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTmark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanelDataProduct2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(187, 187, 187)));

        jLabel7.setText("Category of product");

        jLabel5.setText("Description of product");

        jTdescription.setColumns(20);
        jTdescription.setRows(5);
        jScrollPane1.setViewportView(jTdescription);

        jCcategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCcategoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDataProduct2Layout = new javax.swing.GroupLayout(jPanelDataProduct2);
        jPanelDataProduct2.setLayout(jPanelDataProduct2Layout);
        jPanelDataProduct2Layout.setHorizontalGroup(
            jPanelDataProduct2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataProduct2Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanelDataProduct2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataProduct2Layout.createSequentialGroup()
                        .addGroup(jPanelDataProduct2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7))
                        .addGap(118, 118, 118))
                    .addGroup(jPanelDataProduct2Layout.createSequentialGroup()
                        .addGroup(jPanelDataProduct2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanelDataProduct2Layout.setVerticalGroup(
            jPanelDataProduct2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataProduct2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCcategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanelDataProduct1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelDataProduct2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelDataProduct1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelDataProduct2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26))
        );

        jTableProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "name", "value", "mark", "description", "category"
            }
        ));
        jScrollPane2.setViewportView(jTableProduct);

        jBadd1.setBackground(new java.awt.Color(48, 56, 72));
        jBadd1.setForeground(new java.awt.Color(255, 255, 255));
        jBadd1.setText("Add");
        jBadd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBadd1ActionPerformed(evt);
            }
        });

        jBremove1.setBackground(new java.awt.Color(48, 56, 72));
        jBremove1.setForeground(new java.awt.Color(255, 255, 255));
        jBremove1.setText("Remove");
        jBremove1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBremove1ActionPerformed(evt);
            }
        });

        jBedit1.setBackground(new java.awt.Color(48, 56, 72));
        jBedit1.setForeground(new java.awt.Color(255, 255, 255));
        jBedit1.setText("Edit");
        jBedit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBedit1ActionPerformed(evt);
            }
        });

        jBclear1.setBackground(new java.awt.Color(48, 56, 72));
        jBclear1.setForeground(new java.awt.Color(255, 255, 255));
        jBclear1.setText("Clear");
        jBclear1.setBorderPainted(false);
        jBclear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBclear1ActionPerformed(evt);
            }
        });

        jclose1.setText("Close");

        jBsearch.setBackground(new java.awt.Color(48, 56, 72));
        jBsearch.setForeground(new java.awt.Color(255, 255, 255));
        jBsearch.setText("Search");
        jBsearch.setBorderPainted(false);
        jBsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBsearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jBadd1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBremove1)
                        .addGap(12, 12, 12)
                        .addComponent(jBedit1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBclear1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jclose1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBsearch)))
                .addGap(42, 42, 42))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBadd1)
                    .addComponent(jBremove1)
                    .addComponent(jBedit1)
                    .addComponent(jBclear1)
                    .addComponent(jclose1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBsearch))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTnameActionPerformed

    private void jTmarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTmarkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTmarkActionPerformed

    private void jBaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBaddActionPerformed

    }//GEN-LAST:event_jBaddActionPerformed

    private void jBremoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBremoveActionPerformed

    }//GEN-LAST:event_jBremoveActionPerformed

    private void jBeditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBeditActionPerformed

    }//GEN-LAST:event_jBeditActionPerformed

    private void jBadd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBadd1ActionPerformed
        registerCustumer();
        searchProduct();
        clearFields(jPanelDataProduct1);
        clearFields(jPanelDataProduct2);
    }//GEN-LAST:event_jBadd1ActionPerformed

    private void jBremove1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBremove1ActionPerformed
        remove_costumer();
        searchProduct();
        clearFields(jPanelDataProduct1);
        clearFields(jPanelDataProduct2);
    }//GEN-LAST:event_jBremove1ActionPerformed

    private void jBedit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBedit1ActionPerformed
        initTableListenerProduct();
        changeProductDatas();
        searchProduct();
        clearFields(jPanelDataProduct1);
        clearFields(jPanelDataProduct2);
    }//GEN-LAST:event_jBedit1ActionPerformed

    private void jCcategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCcategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCcategoryActionPerformed

    private void jBsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBsearchActionPerformed
        searchProduct();
    }//GEN-LAST:event_jBsearchActionPerformed

    private void jBclear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBclear1ActionPerformed
        clearFields(jPanelDataProduct1);
        clearFields(jPanelDataProduct2);
    }//GEN-LAST:event_jBclear1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Products.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Products.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Products.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Products.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Products dialog = new Products(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBadd;
    private javax.swing.JButton jBadd1;
    private javax.swing.JButton jBclear;
    private javax.swing.JButton jBclear1;
    private javax.swing.JButton jBedit;
    private javax.swing.JButton jBedit1;
    private javax.swing.JButton jBremove;
    private javax.swing.JButton jBremove1;
    private javax.swing.JButton jBsearch;
    private javax.swing.JComboBox<String> jCcategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLidProduct;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelDataProduct1;
    private javax.swing.JPanel jPanelDataProduct2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableProduct;
    private javax.swing.JTextArea jTdescription;
    private javax.swing.JTextField jTmark;
    private javax.swing.JTextField jTname;
    private javax.swing.JTextField jTsearch;
    private javax.swing.JTextField jTvalue;
    private javax.swing.JButton jclose;
    private javax.swing.JButton jclose1;
    // End of variables declaration//GEN-END:variables
}
