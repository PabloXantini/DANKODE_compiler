package gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class IDETableUI extends JPanel{
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    public IDETableUI(){
        this.model = new DefaultTableModel();
        this.table = new JTable(model);
        this.scrollPane = new JScrollPane(table);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }
    public void setColumns(String... col_titles){
        model.setColumnIdentifiers(col_titles);
    }
    public void addRow(Object... row){
        model.addRow(row);
    }
    public void clear() {
    	model.setRowCount(0);
    }
}
