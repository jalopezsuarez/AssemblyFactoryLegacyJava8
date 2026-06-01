package com.assembly.ui.controls.table;

import javax.swing.JTable;

public interface TableAdapter
{
	public void tableSingleEvent(JTable table, int row, Object value);

	public void tableDoubleEvent(JTable table, int row, Object value);

	public void tableSecondEvent(JTable table, int row, Object value);
}
