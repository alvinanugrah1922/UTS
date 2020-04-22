import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Arsip extends javax.swing.JFrame {

    int idBaris = 0;
    String role;
    DefaultTableModel model;
    byte[] photo = null;
    String filename = null;
//    Login lg=new login();
    private void aturModelTabel() {
        Object[] kolom = {"No", "nama_dokumen", "kategori_dokumen", "lokasi_dokumen", "deskripsi_dokumen", "tanggal"};
        model = new DefaultTableModel(kolom, 0) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        tblData.setModel(model);
        tblData.setRowHeight(20);
        tblData.getColumnModel().getColumn(0).setMinWidth(0);
        tblData.getColumnModel().getColumn(0).setMaxWidth(0);
    }
    
    public Arsip() {
        initComponents();
        aturModelTabel();
        showData("");
    }
    
    private void resetForm() {
        tblData.clearSelection();
        nama_dok.setText("");
        kat_dok.setText("");
        lok_dok.setText("");
        desk_dok.setText("");
        tgl.setText("");
    }

    private void showData(String key) {
        model.getDataVector().removeAllElements();
        String where = "";
        if (!key.isEmpty()) {
            where += "WHERE nama_dokumen LIKE '%" + key + "%' "
                    + "OR kategori_dokumen LIKE '%" + key + "%' "
                    + "OR lokasi_dokumen LIKE '%" + key + "%' "
                    + "OR deskripsi_dokumen LIKE '%" + key + "%' "
                    + "OR tanggal LIKE '%" + key + "%' ";
        }
        String sql = "SELECT * FROM arsip_dokumen " + where;
        Connection con;
        Statement st;
        ResultSet rs;
        int baris = 0;
        try {
            con = koneksi.sambungDB();
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Object kode = rs.getInt(1);
                Object nama = rs.getString(2);
                Object kategori = rs.getString(3);
                Object lokasi = rs.getString(4);
                Object deskripsi = rs.getString(5);
                Object tanggal = rs.getString(6);

                Object[] data = {kode, nama, kategori, lokasi, deskripsi, tanggal};

                model.insertRow(baris, data);

                baris++;

            }
            st.close();
            con.close();
            tblData.revalidate();
            tblData.repaint();
        } catch (SQLException e) {
            System.err.println("showData(): " + e.getMessage());
        }
    }

    private void resetView() {
        resetForm();
        showData("");
        btndelete.setEnabled(false);
        idBaris = 0;
    }
    
    private void pilihData(String n) {
        btndelete.setEnabled(true);
        String sql = "SELECT * FROM arsip_dokumen WHERE kode_dokumen='" + n + "'";
        Connection con;
        Statement st;
        ResultSet rs;
        try {
            con = koneksi.sambungDB();
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int kode = rs.getInt(1);
                String nama = rs.getString(2);
                String kategori = rs.getString(3);
                String lokasi = rs.getString(4);
                String deskripsi = rs.getString(5);
                String tanggal = rs.getString(6);
                

                idBaris = kode;
                nama_dok.setText(nama);
                kat_dok.setText(kategori);
                lok_dok.setText(lokasi);
                desk_dok.setText(deskripsi);
                tgl.setText(tanggal);
            }
            st.close();
            con.close();
        } catch (SQLException e) {
            System.err.println("pilihData(): " + e.getMessage());
        }
    }

    private void simpanData() {
        String nama_dokumen = nama_dok.getText();
        String kategori_dokumen = kat_dok.getText();
        String lokasi_dokumen = lok_dok.getText();
        String deskripsi_dokumen = desk_dok.getText();
        String tanggal = tgl.getText();
        if (nama_dokumen.isEmpty() || kategori_dokumen.isEmpty() || lokasi_dokumen.isEmpty() || deskripsi_dokumen.isEmpty() || tanggal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
        } else {
            String sql
                    = "INSERT INTO arsip_dokumen (nama_dokumen, kategori_dokumen,lokasi_dokumen,"
                    + "deskripsi_dokumen, tanggal) "
                    + "VALUES (\"" + nama_dokumen + "\",\"" + kategori_dokumen + "\","
                    + "\"" + lokasi_dokumen + "\",\"" + deskripsi_dokumen + "\",\"" + tanggal + "\")";
            Connection con;
            Statement st;
            try {
                con = koneksi.sambungDB();
                st = con.createStatement();
                st.executeUpdate(sql);
                st.close();
                con.close();
                resetView();

                JOptionPane.showMessageDialog(this, "Data telah isimpan!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void ubahData() {
        String nama_dokumen = nama_dok.getText();
        String kategori_dokumen = kat_dok.getText();
        String lokasi_dokumen = lok_dok.getText();
        String deskripsi_dokumen = desk_dok.getText();
        String tanggal = tgl.getText();
        if (nama_dokumen.isEmpty() || kategori_dokumen.isEmpty() || lokasi_dokumen.isEmpty() || deskripsi_dokumen.isEmpty() || tanggal.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
        } else {
            String sql = "UPDATE arsip_dokumen "
                    + "SET nama_dokumen=\"" + nama_dokumen + "\","
                    + "kategori_dokumen=\"" + kategori_dokumen + "\","
                    + "lokasi_dokumen=\"" + lokasi_dokumen + "\","
                    + "deskripsi_dokumen=\"" + deskripsi_dokumen + "\","
                    + "tanggal=\"" + tanggal + "\" WHERE kode_dokumen=\"" + idBaris + "\"";
            Connection con;
            Statement st;
            try {
                con = koneksi.sambungDB();
                st = con.createStatement();
                st.executeUpdate(sql);

                st.close();
                con.close();
                resetView();

                JOptionPane.showMessageDialog(this, "Data telah diubah!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void hapusData(int baris) {
        Connection con;
        Statement st;
        try {
            con = koneksi.sambungDB();
            st = con.createStatement();
            st.executeUpdate("DELETE FROM arsip_dokumen WHERE kode_dokumen=" + baris);
            st.close();
            con.close();
            resetView();
            JOptionPane.showMessageDialog(this, "Data telah dihapus");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        nama_dok = new javax.swing.JTextField();
        kat_dok = new javax.swing.JTextField();
        lok_dok = new javax.swing.JTextField();
        desk_dok = new javax.swing.JTextField();
        tgl = new javax.swing.JTextField();
        btnadd = new javax.swing.JButton();
        btnedit = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();
        btnreset = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        logout = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 255, 102));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Sitka Small", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ARSIP DOKUMEN");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(373, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(357, 357, 357))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 40));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Nama Dokumen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(43, 26, 0, 0);
        jPanel3.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Kategori Dokumen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 26, 0, 0);
        jPanel3.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Lokasi Dokumen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 26, 0, 0);
        jPanel3.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Deskripsi Dokumen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 26, 0, 0);
        jPanel3.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Tanggal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 26, 0, 0);
        jPanel3.add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 209;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(43, 36, 0, 0);
        jPanel3.add(nama_dok, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 209;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 36, 0, 0);
        jPanel3.add(kat_dok, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 209;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 36, 0, 0);
        jPanel3.add(lok_dok, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 209;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 36, 0, 0);
        jPanel3.add(desk_dok, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 209;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 41, 0, 0);
        jPanel3.add(tgl, gridBagConstraints);

        btnadd.setBackground(new java.awt.Color(255, 255, 255));
        btnadd.setText("Add");
        btnadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 43;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 83, 0, 0);
        jPanel3.add(btnadd, gridBagConstraints);

        btnedit.setText("Edit");
        btnedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 43;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 41, 0, 0);
        jPanel3.add(btnedit, gridBagConstraints);

        btndelete.setText("Delete");
        btndelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 26, 0, 0);
        jPanel3.add(btndelete, gridBagConstraints);

        btnreset.setText("Reset");
        btnreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnresetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 33;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 43, 0, 0);
        jPanel3.add(btnreset, gridBagConstraints);

        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        tblData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDataMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblData);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 772;
        gridBagConstraints.ipady = 194;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(31, 29, 129, 36);
        jPanel3.add(jScrollPane3, gridBagConstraints);

        logout.setText("Logout");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 29;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 29, 0, 0);
        jPanel3.add(logout, gridBagConstraints);

        jLabel8.setText("Search");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(46, 80, 0, 0);
        jPanel3.add(jLabel8, gridBagConstraints);

        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 118;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(43, 8, 0, 36);
        jPanel3.add(search, gridBagConstraints);

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, 40, 860, 670));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 646, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddActionPerformed
//        try {
//            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/arsip_dokumen","root","");
//            cn.createStatement().executeUpdate("insert into arsip_dokumen values"+"('"+kd_dok.getText()+"','"+nama_dok.getText()+"','"+kat_dok.getText()+"','"+lok_dok.getText()+"','"+desk_dok.getText()+"','"+tgl.getText()+"')");
//            tampilkan();
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null, "Ada data yang belum diisi!");
//        }

         simpanData();
    }//GEN-LAST:event_btnaddActionPerformed

    private void btneditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneditActionPerformed
//        try {
//            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/arsip_dokumen","root","");
//            cn.createStatement().executeUpdate("update arsip_dokumen set nama_dokumen='"+nama_dok.getText()+"',kategori_dokumen='"+kat_dok.getText()+"',lokasi_dokumen='"+lok_dok.getText()+"',deskripsi_dokumen='"+desk_dok.getText()+"',tanggal='"+tgl.getText()+"' where kode_dokumen='"+kd_dok.getText()+"'");
//        } catch (SQLException ex) {
//            Logger.getLogger(Arsip.class.getName()).log(Level.SEVERE, null, ex);
//        }

        ubahData();
    }//GEN-LAST:event_btneditActionPerformed

    private void tblDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDataMouseClicked
//        int i = tblData.getSelectedRow();
//        if(i>1){
//            kd_dok.setText(model.getValueAt(i, 0).toString());
//            nama_dok.setText(model.getValueAt(i, 2).toString());
//            kat_dok.setText(model.getValueAt(i, 3).toString());
//            lok_dok.setText(model.getValueAt(i, 4).toString());
//            desk_dok.setText(model.getValueAt(i, 5).toString());
//            tgl.setText(model.getValueAt(i, 6).toString());
//        }

          role = "Ubah";
        int row = tblData.getRowCount();
        if (row > 0) {
            int sel = tblData.getSelectedRow();
            if (sel != -1) {
                pilihData(tblData.getValueAt(sel, 0).toString());
                btnadd.setText("Add");
            }
        }
    }//GEN-LAST:event_tblDataMouseClicked

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
//        try {
//            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/arsip_dokumen","root","");
//            cn.createStatement().executeUpdate("delete from arsip_dokumen where kode_dokumen ='"+kd_dok.getText()+"'");
//            tampilkan();
//            reset();
//        } catch (SQLException ex) {
//            Logger.getLogger(Arsip.class.getName()).log(Level.SEVERE, null, ex);
//        }

        if (idBaris == 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus !");
        } else {
            hapusData(idBaris);
        }
    }//GEN-LAST:event_btndeleteActionPerformed

    private void btnresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnresetActionPerformed
        reset();
    }//GEN-LAST:event_btnresetActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        Login dp = new Login();
        this.setVisible(false);
        dp.setVisible(true);
    }//GEN-LAST:event_logoutActionPerformed

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        this.dispose();
//        lg.setVisible(true);
    }//GEN-LAST:event_logoutMouseClicked

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
//        DefaultTableModel tabel= (DefaultTableModel)tblData.getModel();
//        String cari = search.getText().toLowerCase();
//        TableRowSorter<DefaultTableModel> tr=new TableRowSorter<DefaultTableModel>(tabel);
//        tblData.setRowSorter(tr);
//        tr.setRowFilter(RowFilter.regexFilter(cari));

        String key = search.getText();
        showData(key);
    }//GEN-LAST:event_searchKeyReleased

    private void reset(){
        nama_dok.setText("");
        kat_dok.setText("");
        lok_dok.setText("");
        desk_dok.setText("");
        tgl.setText("");
    }
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
            java.util.logging.Logger.getLogger(Arsip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Arsip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Arsip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Arsip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Arsip().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnadd;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btnedit;
    private javax.swing.JButton btnreset;
    private javax.swing.JTextField desk_dok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField kat_dok;
    private javax.swing.JButton logout;
    private javax.swing.JTextField lok_dok;
    private javax.swing.JTextField nama_dok;
    private javax.swing.JTextField search;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField tgl;
    // End of variables declaration//GEN-END:variables

//    private void tampilkan() {
//        int row = tblData.getRowCount();
//        for(int a= 0; a<row;a++){
//            model.removeRow(0);
//        }
//        try {
//            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/arsip_dokumen","root","");
//            ResultSet rs = cn.createStatement().executeQuery("SELECT * from arsip_dokumen");
//            while(rs.next()){
//                String data []={rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)};
//                model.addRow(data);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Arsip.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
