/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */

package cubridmanager.cubrid.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cubridmanager.CommonTool;
import cubridmanager.Messages;
import cubridmanager.cubrid.Authorizations;
import cubridmanager.cubrid.SchemaInfo;
import cubridmanager.cubrid.UserInfo;
import cubridmanager.cubrid.view.CubridView;

public class PROPPAGE_USER_AUTHORIZATIONDialog extends Dialog {
	private Shell dlgShell = null;
	private Composite comparent = null;
	private Composite sShell = null;
	private Table LIST_CURRENT_CLASSES = null;
	private Button BUTTON_USERINFO_ADDCLASS = null;
	private Button BUTTON_USERINFO_DELETECLASS = null;
	public Table LIST_AUTHORIZATIONS = null;
	public static final String[] ynstr = { "Y", "N" };
	private List currentClassList = new ArrayList();
	
	public PROPPAGE_USER_AUTHORIZATIONDialog(Shell parent) {
		super(parent);
	}

	public PROPPAGE_USER_AUTHORIZATIONDialog(Shell parent, int style) {
		super(parent, style);
	}

	public Composite SetTabPart(TabFolder parent, boolean isDba) {
		comparent = parent;
		if (isDba) {
			sShell = new Composite(comparent, SWT.NONE); // comment out to use VE
			sShell.setLayout(new GridLayout());
			Label lblItIsDda = new Label(sShell, SWT.WRAP);
			lblItIsDda.setText(Messages.getString("LABEL.ITISDBA"));
			lblItIsDda
					.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
							| GridData.GRAB_VERTICAL
							| GridData.HORIZONTAL_ALIGN_CENTER));

		} else
			createComposite();
		sShell.setParent(parent);
		return sShell;
	}

	public int doModal() {
		createSShell();
		dlgShell.open();

		Display display = dlgShell.getDisplay();
		while (!dlgShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return 0;
	}

	private void createSShell() {
		dlgShell = new Shell(comparent.getShell(), SWT.APPLICATION_MODAL
				| SWT.DIALOG_TRIM);
		dlgShell.setText(Messages
				.getString("TITLE.PROPPAGE_USER_AUTHORIZATIONDIALOG"));
		dlgShell.setLayout(new FillLayout());
		createComposite();
	}

	private void createComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		// sShell = new Composite(dlgShell, SWT.NONE);
		sShell = new Composite(comparent, SWT.NONE); // comment out to use VE
		sShell.setLayout(gridLayout);
		createTable1();

		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		// gridData2.widthHint = 100;
		// gridData2.grabExcessVerticalSpace = true;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		BUTTON_USERINFO_ADDCLASS = new Button(sShell, SWT.NONE);
		BUTTON_USERINFO_ADDCLASS.setText(Messages.getString("BUTTON.ADDCLASS"));
		BUTTON_USERINFO_ADDCLASS.setLayoutData(gridData2);
		BUTTON_USERINFO_ADDCLASS
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						e.doit = false;

						int idx = LIST_CURRENT_CLASSES.getSelectionIndex();
						if (idx < 0)
							return;
						TableItem ti = LIST_CURRENT_CLASSES.getItem(idx);
						if (ti.getText(2).equals(
								PROPPAGE_USER_GENERALDialog.DBUser)) {
							CommonTool.ErrorBox(sShell.getShell(), Messages
									.getString("ERROR.CANNOTGRANTTOYOURSELF"));
							return;
						}
						String addclass = ti.getText(0);
						for (int i = 0, n = LIST_AUTHORIZATIONS.getItemCount(); i < n; i++) {
							if (LIST_AUTHORIZATIONS.getItem(i).getText(0)
									.equals(addclass)) {
								CommonTool
										.ErrorBox(
												sShell.getShell(),
												Messages
														.getString("ERROR.CLASSNAMEALREADYEXIST"));
								return;
							}
						}
						TableItem item = new TableItem(LIST_AUTHORIZATIONS,
								SWT.NONE);
						item.setText(0, addclass);
						item.setText(1, "Y");
						for (int i = 2; i < 15; i++)
							item.setText(i, "N");

						LIST_CURRENT_CLASSES.remove(idx);
						LIST_AUTHORIZATIONS.setFocus();
						LIST_AUTHORIZATIONS
								.setSelection(new TableItem[] { item });
						BUTTON_USERINFO_DELETECLASS.setEnabled(true);
						BUTTON_USERINFO_ADDCLASS.setEnabled(false);
					}
				});

		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		BUTTON_USERINFO_DELETECLASS = new Button(sShell, SWT.NONE);
		BUTTON_USERINFO_DELETECLASS.setText(Messages
				.getString("BUTTON.DELETECLASS"));
		BUTTON_USERINFO_DELETECLASS.setLayoutData(gridData3);
		BUTTON_USERINFO_DELETECLASS
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						e.doit = false;

						int idx = LIST_AUTHORIZATIONS.getSelectionIndex();
						if (idx < 0)
							return;

						String tableName = LIST_AUTHORIZATIONS.getItem(idx)
								.getText(0);
						ArrayList sinfo = SchemaInfo
								.SchemaInfo_get(CubridView.Current_db);
						for (int i = 0, n = sinfo.size(); i < n; i++) {
							SchemaInfo si = (SchemaInfo) sinfo.get(i);
							if (si.name.equals(tableName)) {
								if (si.isSystemClass()) {
									CommonTool
											.WarnBox(
													sShell.getShell(),
													Messages
															.getString("WARNING.CANNOTREVOKESYSTEMCLASS"));
									return;
								}
								TableItem item = new TableItem(
										LIST_CURRENT_CLASSES, SWT.NONE);
								item.setText(0, si.name);
								item
										.setText(
												1,
												si.type.equals("system") ? Messages
														.getString("TREE.SYSSCHEMA")
														: Messages
																.getString("TREE.USERSCHEMA"));
								item.setText(2, si.schemaowner);
								StringBuffer superClass = new StringBuffer("");
								for (int i2 = 0, n2 = si.superClasses.size(); i2 < n2; i2++) {
									if (superClass.length() < 1)
										superClass = superClass
												.append((String) si.superClasses
														.get(i2));
									else {
										superClass = superClass.append(", ");
										superClass = superClass
												.append((String) si.superClasses
														.get(i2));
									}
								}
								item.setText(3, superClass.toString());
								item
										.setText(
												4,
												si.virtual.equals("normal") ? Messages
														.getString("TREE.TABLE")
														: Messages
																.getString("TREE.VIEW"));
								LIST_CURRENT_CLASSES
										.setSelection(new TableItem[] { item });
								break;
							}
						}

						LIST_AUTHORIZATIONS.remove(idx);
						LIST_CURRENT_CLASSES.setFocus();
						BUTTON_USERINFO_DELETECLASS.setEnabled(false);
						BUTTON_USERINFO_ADDCLASS.setEnabled(true);
					}
				});
		createTable2();
		setinfo();
		sShell.pack();
	}

	private void createTable1() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.heightHint = 160;
		gridData.widthHint = 550;
		gridData.grabExcessHorizontalSpace = true;
		LIST_CURRENT_CLASSES = new Table(sShell, SWT.FULL_SELECTION
				| SWT.BORDER);
		LIST_CURRENT_CLASSES.setLinesVisible(true);
		LIST_CURRENT_CLASSES.setLayoutData(gridData);
		LIST_CURRENT_CLASSES.setHeaderVisible(true);
		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(50, 90, true));
		tlayout.addColumnData(new ColumnWeightData(50, 90, true));
		tlayout.addColumnData(new ColumnWeightData(50, 90, true));
		tlayout.addColumnData(new ColumnWeightData(50, 90, true));
		tlayout.addColumnData(new ColumnWeightData(50, 90, true));
		LIST_CURRENT_CLASSES.setLayout(tlayout);
		LIST_CURRENT_CLASSES
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (LIST_CURRENT_CLASSES.getSelectionIndex() > -1)
							BUTTON_USERINFO_ADDCLASS.setEnabled(true);
						else
							BUTTON_USERINFO_ADDCLASS.setEnabled(false);
						BUTTON_USERINFO_DELETECLASS.setEnabled(false);
					}
				});

		LIST_CURRENT_CLASSES
				.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
					public void focusGained(FocusEvent e) {
						if (LIST_CURRENT_CLASSES.getSelectionIndex() > -1)
							BUTTON_USERINFO_ADDCLASS.setEnabled(true);
						else
							BUTTON_USERINFO_ADDCLASS.setEnabled(false);
						BUTTON_USERINFO_DELETECLASS.setEnabled(false);
					}
				});

		TableColumn tblcol = new TableColumn(LIST_CURRENT_CLASSES, SWT.LEFT);
		tblcol.setText(Messages.getString("TABLE.NAME"));
		final ColumnComparator nameComparator = new ColumnComparator(ColumnComparator.NAMECOLUMN, true);
		tblcol.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableColumn column = (TableColumn) e.widget;
				LIST_CURRENT_CLASSES.setSortColumn(column);
				LIST_CURRENT_CLASSES.setSortDirection(nameComparator.isAsc() ? SWT.UP : SWT.DOWN);
				Collections.sort(currentClassList, nameComparator);
				nameComparator.setAsc(!nameComparator.isAsc());
				makeCurrentClassTableItem();
			}
		});
		tblcol = new TableColumn(LIST_CURRENT_CLASSES, SWT.LEFT);
		tblcol.setText(Messages.getString("TABLE.SCHEMATYPE"));
		tblcol = new TableColumn(LIST_CURRENT_CLASSES, SWT.LEFT);
		tblcol.setText(Messages.getString("TABLE.OWNER"));
		final ColumnComparator ownerComparator = new ColumnComparator(ColumnComparator.OWNERCOLUMN, true);
		tblcol.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableColumn column = (TableColumn) e.widget;
				LIST_CURRENT_CLASSES.setSortColumn(column);
				LIST_CURRENT_CLASSES.setSortDirection(ownerComparator.isAsc() ? SWT.UP : SWT.DOWN);
				Collections.sort(currentClassList, ownerComparator);
				ownerComparator.setAsc(!ownerComparator.isAsc());
				makeCurrentClassTableItem();
			}
		});
		tblcol = new TableColumn(LIST_CURRENT_CLASSES, SWT.LEFT);
		tblcol.setText(Messages.getString("TABLE.SUPERCLASS"));
		tblcol = new TableColumn(LIST_CURRENT_CLASSES, SWT.LEFT);
		tblcol.setText(Messages.getString("TABLE.VIRTUAL"));
	}

	private void createTable2() {
		final TableViewer tv = new TableViewer(sShell, SWT.FULL_SELECTION | SWT.BORDER);
		LIST_AUTHORIZATIONS = tv.getTable();
		LIST_AUTHORIZATIONS.setBounds(new org.eclipse.swt.graphics.Rectangle(
				14, 228, 576, 160));
		LIST_AUTHORIZATIONS.setLinesVisible(true);
		LIST_AUTHORIZATIONS.setHeaderVisible(true);
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.heightHint = 160;
		gridData.widthHint = 550;
		gridData.grabExcessHorizontalSpace = true;
		LIST_AUTHORIZATIONS.setLayoutData(gridData);

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(20, 100, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		tlayout.addColumnData(new ColumnWeightData(10, 70, true));
		LIST_AUTHORIZATIONS.setLayout(tlayout);

		TableColumn tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.CLASS"));
		tblColumn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tv.setSorter(null);
			}
		});
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.SELECT"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.INSERT"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.UPDATE"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.DELETE"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.ALTER"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.INDEX1"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.EXECUTE"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTSELECT"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTINSERT"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTUPDATE"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTDELETE"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTALTER"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTINDEX"));
		tblColumn = new TableColumn(LIST_AUTHORIZATIONS, SWT.LEFT);
		tblColumn.setText(Messages.getString("TABLE.GRANTEXECUTE"));

		final TableEditor editor = new TableEditor(LIST_AUTHORIZATIONS);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		LIST_AUTHORIZATIONS.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if (LIST_AUTHORIZATIONS.getSelectionCount() > 0) {
					TableItem[] items = LIST_AUTHORIZATIONS.getSelection();
					List sinfo = SchemaInfo.SchemaInfo_get(CubridView.Current_db);
					for (int i = 0, n = sinfo.size(); i < n; i++) {
						SchemaInfo si = (SchemaInfo) sinfo.get(i);
						if (si.name.equals(items[0].getText(0))) {
							if (si.isSystemClass())
								return;
						}
					}
				} else {
					return;
				}
				Control old = editor.getEditor();
				if (old != null)
					old.dispose();

				Point pt = new Point(event.x, event.y);

				final TableItem item = LIST_AUTHORIZATIONS.getItem(pt);

				if (item != null) {
					int column = -1;
					for (int i = 0, n = LIST_AUTHORIZATIONS.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							column = i;
							break;
						}
					}
					if (column == 0) {
						return;
					} else {
						final CCombo combo = new CCombo(LIST_AUTHORIZATIONS, SWT.READ_ONLY);
						combo.setItems(ynstr);
						combo.select(combo.indexOf(item.getText(column)));
						combo.setFocus();
						editor.setEditor(combo, item, column);
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent event) {
								item.setText(col, combo.getText());
								combo.dispose();
							}
						});
						item.addDisposeListener(new DisposeListener() {
							public void widgetDisposed(DisposeEvent e) {
								if (combo != null && !combo.isDisposed()) {
									combo.dispose();
								}
							}
						});
					}
				}
			}
		});		
		LIST_AUTHORIZATIONS
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (LIST_AUTHORIZATIONS.getSelectionIndex() > -1)
							BUTTON_USERINFO_DELETECLASS.setEnabled(true);
						else
							BUTTON_USERINFO_DELETECLASS.setEnabled(false);
						BUTTON_USERINFO_ADDCLASS.setEnabled(false);
					}
				});

		LIST_AUTHORIZATIONS
				.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
					public void focusGained(FocusEvent e) {
						if (LIST_AUTHORIZATIONS.getSelectionIndex() > -1)
							BUTTON_USERINFO_DELETECLASS.setEnabled(true);
						else
							BUTTON_USERINFO_DELETECLASS.setEnabled(false);
						BUTTON_USERINFO_ADDCLASS.setEnabled(false);
					}
				});
	}

	private void makeCurrentClassTableItem() {
		LIST_CURRENT_CLASSES.removeAll();
		for (int i = 0, n = currentClassList.size(); i < n; i++) {
			SchemaInfo si = (SchemaInfo) currentClassList.get(i);
			if (!si.type.equals("user"))
				continue;
			TableItem item = new TableItem(LIST_CURRENT_CLASSES, SWT.NONE);
			item.setText(0, si.name);
			item.setText(1, si.type.equals("system") ? Messages.getString("TREE.SYSSCHEMA") : Messages
			        .getString("TREE.USERSCHEMA"));
			item.setText(2, si.schemaowner);
			String superstr = new String("");
			for (int i2 = 0, n2 = si.superClasses.size(); i2 < n2; i2++) {
				if (superstr.length() < 1)
					superstr = superstr.concat((String) si.superClasses.get(i2));
				else
					superstr = superstr.concat(", " + (String) si.superClasses.get(i2));
			}
			item.setText(3, superstr);
			item.setText(4, si.virtual.equals("normal") ? Messages.getString("TREE.TABLE") : Messages
			        .getString("TREE.VIEW"));
		}
		
		for (int i = LIST_CURRENT_CLASSES.getItemCount() - 1, n = -1; i > n; i--) {
			for (int j = 0, m = LIST_AUTHORIZATIONS.getItemCount(); j < m; j++) {
				if (LIST_CURRENT_CLASSES.getItem(i).getText(0).equals(
						LIST_AUTHORIZATIONS.getItem(j).getText(0))) {
					LIST_CURRENT_CLASSES.remove(i);
					break;
				}
			}
		}
	}
	
	private void setinfo() {
		ArrayList sinfo = SchemaInfo.SchemaInfo_get(CubridView.Current_db);
		currentClassList.addAll(sinfo);
		makeCurrentClassTableItem();

		if (PROPPAGE_USER_GENERALDialog.DBUser == null
				|| PROPPAGE_USER_GENERALDialog.DBUser.length() <= 0)
			return;
		ArrayList userinfo = UserInfo.UserInfo_get(CubridView.Current_db);
		UserInfo ui = UserInfo.UserInfo_find(userinfo,
				PROPPAGE_USER_GENERALDialog.DBUser);

		TableItem item;

		ArrayList alreadyGrantTable = new ArrayList();
		for (int i = 0, n = ui.authorizations.size(); i < n; i++) {
			Authorizations auth = (Authorizations) ui.authorizations.get(i);
			alreadyGrantTable.add(auth.className);
			item = new TableItem(LIST_AUTHORIZATIONS, SWT.NONE);
			item.setText(0, auth.className);
			item.setText(1, CommonTool.BooleanYN(auth.selectPriv));
			item.setText(2, CommonTool.BooleanYN(auth.insertPriv));
			item.setText(3, CommonTool.BooleanYN(auth.updatePriv));
			item.setText(4, CommonTool.BooleanYN(auth.deletePriv));
			item.setText(5, CommonTool.BooleanYN(auth.alterPriv));
			item.setText(6, CommonTool.BooleanYN(auth.indexPriv));
			item.setText(7, CommonTool.BooleanYN(auth.executePriv));
			item.setText(8, CommonTool.BooleanYN(auth.grantSelectPriv));
			item.setText(9, CommonTool.BooleanYN(auth.grantInsertPriv));
			item.setText(10, CommonTool.BooleanYN(auth.grantUpdatePriv));
			item.setText(11, CommonTool.BooleanYN(auth.grantDeletePriv));
			item.setText(12, CommonTool.BooleanYN(auth.grantAlterPriv));
			item.setText(13, CommonTool.BooleanYN(auth.grantIndexPriv));
			item.setText(14, CommonTool.BooleanYN(auth.grantExecutePriv));
		}

		for (int i = LIST_CURRENT_CLASSES.getItemCount() - 1, n = -1; i > n; i--) {
			for (int j = 0, m = alreadyGrantTable.size(); j < m; j++) {
				if (LIST_CURRENT_CLASSES.getItem(i).getText(0).equals(
						alreadyGrantTable.get(j))) {
					LIST_CURRENT_CLASSES.remove(i);
					alreadyGrantTable.remove(j);
					alreadyGrantTable.trimToSize();
					break;
				}
			}
		}

		for (int i = 0, n = LIST_CURRENT_CLASSES.getColumnCount(); i < n; i++) {
			LIST_CURRENT_CLASSES.getColumn(i).pack();
		}
		for (int i = 0, n = LIST_AUTHORIZATIONS.getColumnCount(); i < n; i++) {
			LIST_AUTHORIZATIONS.getColumn(i).pack();
		}

		BUTTON_USERINFO_ADDCLASS.setEnabled(false);
		BUTTON_USERINFO_DELETECLASS.setEnabled(false);
	}
}

class ColumnComparator implements Comparator {

	public static final String NAMECOLUMN = "name";
	public static final String OWNERCOLUMN = "owner";
	private String columnName = null;
	private boolean isAsc = true;

	public ColumnComparator(String columnName,boolean isAsc) {
		this.columnName = columnName;
		this.isAsc = isAsc;
	}

	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}

	public boolean isAsc() {
		return this.isAsc;
	}

	public int compare(Object o1, Object o2) {
		SchemaInfo schemaInfo1 = (SchemaInfo) o1;
		SchemaInfo schemaInfo2 = (SchemaInfo) o2;
		String str1 = "";
		String str2 = "";
		if (columnName.equals(NAMECOLUMN)) {
			str1 = schemaInfo1.name;
			str2 = schemaInfo2.name;
		} else if (columnName.equals(OWNERCOLUMN)) {
			str1 = schemaInfo1.schemaowner;
			str2 = schemaInfo2.schemaowner;
		}
		return isAsc ? str1.compareTo(str2) : str2.compareTo(str1);
	}
}


