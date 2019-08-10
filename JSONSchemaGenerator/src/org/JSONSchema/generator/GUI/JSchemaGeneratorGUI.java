package org.JSONSchema.generator.GUI;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.JSONSchema.generator.core.ImportJSON;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.browser.Browser;

public class JSchemaGeneratorGUI extends Composite {
	private Text jsonText;
	private Text schemaText;
	/**
	 * @wbp.nonvisual location=314,279
	 */
	public static final Display display = new Display();
	public static final Shell shell = new Shell(display);
	int nameIndex=0,titleIndex=2,typeIndex=3,descIndex=1,egIndex=4,enumIndex=5,formatIndex=6;
	public static void main(String[] args) {
		//Display display = new Display();
		//Shell shell = new Shell(display);
		shell.setMinimumSize(new Point(800, 600));
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		JSchemaGeneratorGUI jschema=new JSchemaGeneratorGUI(shell, SWT.NONE);
		shell.setText("JSON Schema Tool");
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	private final JFileChooser fileChooser = new JFileChooser();
	private Tree tree;
	private TableEditor editor;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public JSchemaGeneratorGUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			    "JSON files", "JSON");
		fileChooser.setFileFilter(filter);
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		
		TabItem tbtmEditor = new TabItem(tabFolder, SWT.NONE);
		tbtmEditor.setText("Editor");
		
		Font treeFont = new Font( display, new FontData( "Segoe UI", 10, SWT.NORMAL ) );
		
		Composite editorComposite = new Composite(tabFolder, SWT.NONE);
		tbtmEditor.setControl(editorComposite);
		editorComposite.setLayout(new GridLayout(1, false));
		
		tree = new Tree(editorComposite, SWT.MULTI | SWT.FULL_SELECTION);
		//editor = new TableEditor(tree);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeColumn trclmnName = new TreeColumn(tree, SWT.CENTER);
		trclmnName.setWidth(190);
		trclmnName.setText("Name");
		
		TreeColumn trclmnDescription = new TreeColumn(tree, SWT.CENTER);
		trclmnDescription.setWidth(391);
		trclmnDescription.setText("Description");
		
		TreeColumn trclmnTitle = new TreeColumn(tree, SWT.CENTER);
		trclmnTitle.setWidth(130);
		trclmnTitle.setText("Title");
		
		TreeColumn trclmnType = new TreeColumn(tree, SWT.CENTER);
		trclmnType.setWidth(120);
		trclmnType.setText("Type");
		
		TreeColumn trclmnExample = new TreeColumn(tree, SWT.CENTER);
		trclmnExample.setWidth(231);
		trclmnExample.setText("Example");
		
		TreeColumn trclmnEnum = new TreeColumn(tree, SWT.CENTER);
		trclmnEnum.setWidth(100);
		trclmnEnum.setText("Enum");
		tree.setFont(treeFont);
		tree.addListener(SWT.EraseItem, new Listener() {
		      public void handleEvent(Event event) {
		        if ((event.detail & SWT.SELECTED) != 0) {
		          GC gc = event.gc;
		          Rectangle area = tree.getClientArea();
		          /*
		           * If you wish to paint the selection beyond the end of last column,
		           * you must change the clipping region.
		           */
		          int columnCount = tree.getColumnCount();
		          if (event.index == columnCount - 1 || columnCount == 0) {
		            int width = area.x + area.width + event.x;
		            if (width > 0) {
		              Region region = new Region();
		              gc.getClipping(region);
		              region.add(event.x, event.y, width, event.height);
		              gc.setClipping(region);
		              region.dispose();
		            }
		          }
		          gc.setAdvanced(true);
		          if (gc.getAdvanced())
		            gc.setAlpha(128);
		          Rectangle rect = event.getBounds();
		          Color foreground = gc.getForeground();
		          Color background = gc.getBackground();
		          gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
		          gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		          gc.fillGradientRectangle(0, rect.y, shell.getSize().x, rect.height, false);
		          // restore colors for subsequent drawing
		          gc.setForeground(foreground);
		          gc.setBackground(background);
		          event.detail &= ~SWT.SELECTED;
		        }
		      }
		    });
		
	    final TreeEditor editor = new TreeEditor(tree);
	    
	    TreeColumn trclmnFormat = new TreeColumn(tree, SWT.CENTER);
	    trclmnFormat.setText("Format");
	    trclmnFormat.setWidth(100);
	    
	    tree.addListener(SWT.MouseDoubleClick, new Listener() {
	        public void handleEvent(Event event) {
	          //event.height = 67;
	          //PointerInfo pi = MouseInfo.getPointerInfo();
	          //tree.getItem(new Point((int)pi.getLocation().getX(),(int) pi.getLocation().getY()));
	          //tree.getColumns()[0].get
	          //shell.setText(event.count + " was selected");
	          final TreeItem item = tree.getSelection()[0];
	          int colIndex=0;
	          System.out.println(event.getBounds().x+","+event.getBounds().y);
	          for(int i=0;i<tree.getColumnCount();i++) {
	        	  //System.out.println(i);
	        	 if(item.getBounds(i).contains(event.getBounds().x,event.getBounds().y))
	        		 colIndex=i;
	          }
	          final int colInd=colIndex;
	          
	          if(colInd==formatIndex) {
	        	  Combo formatCombo = null;
	        	  String type=item.getText(typeIndex);
	        	  
	        	  switch (type) {
					case "number":
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {"", "double", "float"});
						break;
					case "string":
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {"", "binary", "byte", "password", "email", "uuid", "uri", "hostname", "ipv4", "ipv6"});
						break;
					case "integer":
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {"", "int32", "int64"});
						break;
					case "array":
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {""});
						break;
					case "object":
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {""});
						break;
					case "boolean":
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {"","1/0", "true/false","TRUE/FALSE","yes/no","YES/NO"});
						break;
					case "date":
						formatCombo=new Combo(tree, SWT.NONE);
						formatCombo.setTouchEnabled(true);
						break;
					default:
						formatCombo=new Combo(tree, SWT.READ_ONLY);
						formatCombo.setItems(new String[] {"","Select Type first"});
						break;
				  }
		          formatCombo.setText(item.getText(colIndex).toLowerCase());
		          //text.selectAll();
		          formatCombo.setFocus();
	
		          final Combo fc=formatCombo;
		          formatCombo.addFocusListener(new FocusAdapter() {
		            public void focusLost(FocusEvent event) {
		              item.setText(colInd,fc.getText().toLowerCase());
		              fc.dispose();
		            }
		          });
	
		          formatCombo.addKeyListener(new KeyAdapter() {
		            public void keyPressed(KeyEvent event) {
		              switch (event.keyCode) {
		              case SWT.CR:
		                item.setText(colInd,fc.getText().toLowerCase());
		              case SWT.ESC:
		                fc.dispose();
		                break;
		              }
		            }
		          });
		          editor.setEditor(formatCombo, item,colInd);
	          }else
	          
	          if(colInd==typeIndex) {
		          Combo typeCombo = new Combo(tree, SWT.READ_ONLY);
		          if(item.getItemCount()==0)
		        	  typeCombo.setItems(new String[] {"string", "number", "boolean", "integer", "object", "array", "date"});
		          else
		        	  typeCombo.setItems(new String[] {"object", "array"});
		  		  //final Text text = new Text(tree, SWT.NONE);
		          typeCombo.setText(item.getText(colIndex).toLowerCase());
		          //text.selectAll();
		          typeCombo.setFocus();
		          
		          typeCombo.addFocusListener(new FocusAdapter() {
		            public void focusLost(FocusEvent event) {
		              if(!item.getText(colInd).equalsIgnoreCase(typeCombo.getText())) {
		            	  item.setText(formatIndex,"");
			              item.setText(colInd,typeCombo.getText().toLowerCase());
			              if(item.getText(formatIndex).trim().length()==0) {
				              if(typeCombo.getText().equals("date"))
				                	 item.setText(formatIndex,"dd-MM-yyyy HH:mm:ss");
			              }
		              }
		              typeCombo.dispose();
		            }
		          });
	
		          typeCombo.addKeyListener(new KeyAdapter() {
		            public void keyPressed(KeyEvent event) {
		              switch (event.keyCode) {
		              case SWT.CR:
		                item.setText(colInd,typeCombo.getText().toLowerCase());
		              case SWT.ESC:
		                typeCombo.dispose();
		                break;
		              }
		            }
		          });
		          editor.setEditor(typeCombo, item,colInd);
	          }else {
		    	  final Text text = new Text(tree, SWT.NONE);
		          text.setText(item.getText(colIndex));
		          text.selectAll();
		          text.setFocus();

		          text.addFocusListener(new FocusAdapter() {
		            public void focusLost(FocusEvent event) {
		              item.setText(colInd,text.getText());
		              
		              text.dispose();
		            }
		          });
	
		          text.addKeyListener(new KeyAdapter() {
		            public void keyPressed(KeyEvent event) {
		              switch (event.keyCode) {
		              case SWT.CR:
		            	item.setText(colInd,text.getText());
		              case SWT.ESC:
		                text.dispose();
		                break;
		              }
		            }
		          });
		          if(colInd==descIndex || colInd==egIndex || colInd==enumIndex) {
		        	  CustomDialogBox cdb=new CustomDialogBox(shell, SWT.NONE);
		        	  cdb.open(item,colInd);
		          }else
		        	  editor.setEditor(text, item,colInd);	 
	          }
	        }
	        
	      });	   
	    // */
	    
		SashForm sashForm_2 = new SashForm(editorComposite, SWT.NONE);
		sashForm_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Button btnUpdateSchema = new Button(sashForm_2, SWT.NONE);
		btnUpdateSchema.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem items[]= tree.getItems();
				System.out.println(items.length+"");
				jsonWorkspace="";
				try {
					newSchema(1);
					jsonWorkspace=jsonWorkspace.replaceAll(","+identifier, "");
			        jsonWorkspace=jsonWorkspace.replaceAll(identifier, "");
			        //System.out.println(jsonWorkspace);
			        jsonWorkspace=ImportJSON.beautify(jsonWorkspace);
					schemaText.setText(jsonWorkspace);
				} catch (Exception e2) {
					new ErrorDialogBox(shell, style).open(e2);
				}
				
			}
		});
		btnUpdateSchema.setText("Update Schema");
		
		Button btnAdd = new Button(sashForm_2, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(tree.getSelection().length<=0) {
						TreeItem item = new TreeItem(tree, SWT.FULL_SELECTION);
						item.setText(nameIndex, "newElement");
						item.setText(descIndex, "Double click on any cell to update");
						item.setText(titleIndex, "Add Title here");
						item.setText(typeIndex, "string");
						item.setText(egIndex, "Add example");
						item.setText(enumIndex, "example1,example2");
					}else {
						final TreeItem selItem = tree.getSelection()[0];
						String type=selItem.getText(typeIndex);
						if(type.equalsIgnoreCase("array") ||  type.equalsIgnoreCase("object")) {
							TreeItem item = new TreeItem(selItem, SWT.FULL_SELECTION);
							item.setText(nameIndex, "newElement");
							item.setText(descIndex, "Double click on any cell to update");
							item.setText(titleIndex, "Add Title here");
							item.setText(typeIndex, "string");
							item.setText(egIndex, "Add example");
							item.setText(enumIndex, "example1,example2");
						}else {
							TreeItem item = null;
							if(selItem.getParentItem()!=null)
								item=new TreeItem(selItem.getParentItem(), SWT.FULL_SELECTION);
							else
								item=new TreeItem(tree, SWT.FULL_SELECTION);
							item.setText(nameIndex, "newElement");
							item.setText(descIndex, "Double click on any cell to update");
							item.setText(titleIndex, "Add Title here");
							item.setText(typeIndex, "string");
							item.setText(egIndex, "Add example");
							item.setText(enumIndex, "example1,example2");
						}
					}
				} catch (Exception e2) {
					new ErrorDialogBox(shell, style).open(e2);
				}
				
			}
		});
		btnAdd.setText("Add");
		
		Button btnRemove = new Button(sashForm_2, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(tree.getSelection().length==0)
						new InfoDialogBox(shell, style).open("Please select the row you want to remove. Selected row will be highlighted with red color.\n\nNote:-\nYou can't undo remove action.");
					else {
						final TreeItem item = tree.getSelection()[0];
						item.dispose();
					}
				} catch (Exception e2) {
					new ErrorDialogBox(shell, style).open(e2);
				}
				
			}
		});
		btnRemove.setText("Remove");
		sashForm_2.setWeights(new int[] {1, 1, 1});
		
		TabItem tbtmGenerator = new TabItem(tabFolder, SWT.NONE);
		tbtmGenerator.setText("Generator");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmGenerator.setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		
		SashForm sashForm_3 = new SashForm(composite, SWT.NONE);
		
		Label lblNewLabel = new Label(sashForm_3, SWT.NONE);
		lblNewLabel.setText("JSON Payload");
		sashForm_3.setWeights(new int[] {1});
		
		SashForm sashForm_4 = new SashForm(composite, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(sashForm_4, SWT.NONE);
		lblNewLabel_1.setText("JSON Schema");
		sashForm_4.setWeights(new int[] {1});
		
		jsonText = new Text(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		jsonText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		schemaText = new Text(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		schemaText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		SashForm sashForm = new SashForm(composite, SWT.NONE);
		
		Button generatePayload = new Button(sashForm, SWT.NONE);
		generatePayload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String[] text= ImportJSON.getInstance().generatePayload(schemaText.getText());
					schemaText.setText(text[0]);
					jsonText.setText(text[1]);
					Map<String, Object> map= ImportJSON.getInstance().schemaMap;
					//tree.clearAll(false);
					tree.removeAll();
					loadSchemaEditor(map, tree);
				} catch (Exception e1) {
					if(schemaText.getText()==null || schemaText.getText().length()==0) {
						new InfoDialogBox(shell, style).open("To generate JSON Payload you need to load JSON Schema first.\n1. Use \"Browse JSON Schema\" button to select JSON Schema file.\nOr\n2. Paste JSON Schema text into \"JSON Schema\" text box.");
					}else
					new ErrorDialogBox(shell, style).open(e1);
				}
			}
		});
		generatePayload.setText("Generate JSON Payload");
		
		Button browseJSONPayloadFle = new Button(sashForm, SWT.NONE);
		browseJSONPayloadFle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String filePath=openFileDialog();
					if(filePath !=null) {
						   System.out.println("You chose to open this file: " + filePath);
							String[] text= ImportJSON.getInstance().loadPayload(filePath);
							schemaText.setText(text[0]);
							jsonText.setText(text[1]);
							ImportJSON.getInstance().generatePayload(schemaText.getText());
							Map<String, Object> map=ImportJSON.getInstance().schemaMap;
							tree.removeAll();
							loadSchemaEditor(map, tree);
					}
				} catch (Exception e2) {
					new ErrorDialogBox(shell, style).open(e2);
				}
				
			}
		});
		browseJSONPayloadFle.setText("Browse JSON Payload");
		SashForm sashForm_1 = new SashForm(composite, SWT.NONE);
		Button generateSchema = new Button(sashForm_1, SWT.NONE);
		generateSchema.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String retStr[]=ImportJSON.getInstance().generateSchema(jsonText.getText());
					schemaText.setText(retStr[0]);
					jsonText.setText(retStr[1]);
					ImportJSON.getInstance().generatePayload(schemaText.getText());
					Map<String, Object> map=ImportJSON.getInstance().schemaMap;
					tree.removeAll();
					loadSchemaEditor(map, tree);
				} catch (Exception e1) {
					if(jsonText.getText()==null || jsonText.getText().length()==0) {
						new InfoDialogBox(shell, style).open("To generate JSON Schema you need to load JSON Payload first.\n1. Use \"Browse JSON Payload\" button to select JSON Payload file.\nOr\n2. Paste JSON Payload text into \"JSON Payload\" text box.");
					}else
					new ErrorDialogBox(shell, style).open(e1);
				}
			}
		});
		generateSchema.setText("Generate JSON Schema");
		
		Button browseSchemaFile = new Button(sashForm_1, SWT.NONE);
		browseSchemaFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String filePath=openFileDialog();
					if(filePath !=null) {
						   System.out.println("You chose to open this file: " + filePath);
							String[] text= ImportJSON.getInstance().loadSchema(filePath);
							Map<String, Object> map=ImportJSON.getInstance().schemaMap;
							schemaText.setText(text[0]);
							jsonText.setText(text[1]);
							//tree.clearAll(false);
							tree.removeAll();
							loadSchemaEditor(map, tree);
					}
				} catch (Exception e2) {
					new ErrorDialogBox(shell, style).open(e2);
				}
				
			}
		});
		browseSchemaFile.setText("Browse JSON Schema");
		sashForm_1.setWeights(new int[] {1, 1});
		
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
		
		TabItem tbtmAbout = new TabItem(tabFolder, SWT.NONE);
		tbtmAbout.setText("About");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmAbout.setControl(composite_1);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Browser browser = new Browser(composite_1, SWT.NONE);
		browser.setText("<!DOCTYPE html>\r\n<html>\r\n<title>W3.CSS</title>\r\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n\r\n<head>\r\n<style>\r\n/* W3.CSS 4.13 June 2019 by Jan Egil and Borge Refsnes */\r\nhtml{box-sizing:border-box}*,*:before,*:after{box-sizing:inherit}\r\n/* Extract from normalize.css by Nicolas Gallagher and Jonathan Neal git.io/normalize */\r\nhtml{-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}body{margin:0}\r\narticle,aside,details,figcaption,figure,footer,header,main,menu,nav,section{display:block}summary{display:list-item}\r\naudio,canvas,progress,video{display:inline-block}progress{vertical-align:baseline}\r\naudio:not([controls]){display:none;height:0}[hidden],template{display:none}\r\na{background-color:transparent}a:active,a:hover{outline-width:0}\r\nabbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted}\r\nb,strong{font-weight:bolder}dfn{font-style:italic}mark{background:#ff0;color:#000}\r\nsmall{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}\r\nsub{bottom:-0.25em}sup{top:-0.5em}figure{margin:1em 40px}img{border-style:none}\r\ncode,kbd,pre,samp{font-family:monospace,monospace;font-size:1em}hr{box-sizing:content-box;height:0;overflow:visible}\r\nbutton,input,select,textarea,optgroup{font:inherit;margin:0}optgroup{font-weight:bold}\r\nbutton,input{overflow:visible}button,select{text-transform:none}\r\nbutton,[type=button],[type=reset],[type=submit]{-webkit-appearance:button}\r\nbutton::-moz-focus-inner,[type=button]::-moz-focus-inner,[type=reset]::-moz-focus-inner,[type=submit]::-moz-focus-inner{border-style:none;padding:0}\r\nbutton:-moz-focusring,[type=button]:-moz-focusring,[type=reset]:-moz-focusring,[type=submit]:-moz-focusring{outline:1px dotted ButtonText}\r\nfieldset{border:1px solid #c0c0c0;margin:0 2px;padding:.35em .625em .75em}\r\nlegend{color:inherit;display:table;max-width:100%;padding:0;white-space:normal}textarea{overflow:auto}\r\n[type=checkbox],[type=radio]{padding:0}\r\n[type=number]::-webkit-inner-spin-button,[type=number]::-webkit-outer-spin-button{height:auto}\r\n[type=search]{-webkit-appearance:textfield;outline-offset:-2px}\r\n[type=search]::-webkit-search-decoration{-webkit-appearance:none}\r\n::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}\r\n/* End extract */\r\nhtml,body{font-family:Verdana,sans-serif;font-size:15px;line-height:1.5}html{overflow-x:hidden}\r\nh1{font-size:36px}h2{font-size:30px}h3{font-size:24px}h4{font-size:20px}h5{font-size:18px}h6{font-size:16px}.w3-serif{font-family:serif}\r\nh1,h2,h3,h4,h5,h6{font-family:\"Segoe UI\",Arial,sans-serif;font-weight:400;margin:10px 0}.w3-wide{letter-spacing:4px}\r\nhr{border:0;border-top:1px solid #eee;margin:20px 0}\r\n.w3-image{max-width:100%;height:auto}img{vertical-align:middle}a{color:inherit}\r\n.w3-table,.w3-table-all{border-collapse:collapse;border-spacing:0;width:100%;display:table}.w3-table-all{border:1px solid #ccc}\r\n.w3-bordered tr,.w3-table-all tr{border-bottom:1px solid #ddd}.w3-striped tbody tr:nth-child(even){background-color:#f1f1f1}\r\n.w3-table-all tr:nth-child(odd){background-color:#fff}.w3-table-all tr:nth-child(even){background-color:#f1f1f1}\r\n.w3-hoverable tbody tr:hover,.w3-ul.w3-hoverable li:hover{background-color:#ccc}.w3-centered tr th,.w3-centered tr td{text-align:center}\r\n.w3-table td,.w3-table th,.w3-table-all td,.w3-table-all th{padding:8px 8px;display:table-cell;text-align:left;vertical-align:top}\r\n.w3-table th:first-child,.w3-table td:first-child,.w3-table-all th:first-child,.w3-table-all td:first-child{padding-left:16px}\r\n.w3-btn,.w3-button{border:none;display:inline-block;padding:8px 16px;vertical-align:middle;overflow:hidden;text-decoration:none;color:inherit;background-color:inherit;text-align:center;cursor:pointer;white-space:nowrap}\r\n.w3-btn:hover{box-shadow:0 8px 16px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19)}\r\n.w3-btn,.w3-button{-webkit-touch-callout:none;-webkit-user-select:none;-khtml-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none}   \r\n.w3-disabled,.w3-btn:disabled,.w3-button:disabled{cursor:not-allowed;opacity:0.3}.w3-disabled *,:disabled *{pointer-events:none}\r\n.w3-btn.w3-disabled:hover,.w3-btn:disabled:hover{box-shadow:none}\r\n.w3-badge,.w3-tag{background-color:#000;color:#fff;display:inline-block;padding-left:8px;padding-right:8px;text-align:center}.w3-badge{border-radius:50%}\r\n.w3-ul{list-style-type:none;padding:0;margin:0}.w3-ul li{padding:8px 16px;border-bottom:1px solid #ddd}.w3-ul li:last-child{border-bottom:none}\r\n.w3-tooltip,.w3-display-container{position:relative}.w3-tooltip .w3-text{display:none}.w3-tooltip:hover .w3-text{display:inline-block}\r\n.w3-ripple:active{opacity:0.5}.w3-ripple{transition:opacity 0s}\r\n.w3-input{padding:8px;display:block;border:none;border-bottom:1px solid #ccc;width:100%}\r\n.w3-select{padding:9px 0;width:100%;border:none;border-bottom:1px solid #ccc}\r\n.w3-dropdown-click,.w3-dropdown-hover{position:relative;display:inline-block;cursor:pointer}\r\n.w3-dropdown-hover:hover .w3-dropdown-content{display:block}\r\n.w3-dropdown-hover:first-child,.w3-dropdown-click:hover{background-color:#ccc;color:#000}\r\n.w3-dropdown-hover:hover > .w3-button:first-child,.w3-dropdown-click:hover > .w3-button:first-child{background-color:#ccc;color:#000}\r\n.w3-dropdown-content{cursor:auto;color:#000;background-color:#fff;display:none;position:absolute;min-width:160px;margin:0;padding:0;z-index:1}\r\n.w3-check,.w3-radio{width:24px;height:24px;position:relative;top:6px}\r\n.w3-sidebar{height:100%;width:200px;background-color:#fff;position:fixed!important;z-index:1;overflow:auto}\r\n.w3-bar-block .w3-dropdown-hover,.w3-bar-block .w3-dropdown-click{width:100%}\r\n.w3-bar-block .w3-dropdown-hover .w3-dropdown-content,.w3-bar-block .w3-dropdown-click .w3-dropdown-content{min-width:100%}\r\n.w3-bar-block .w3-dropdown-hover .w3-button,.w3-bar-block .w3-dropdown-click .w3-button{width:100%;text-align:left;padding:8px 16px}\r\n.w3-main,#main{transition:margin-left .4s}\r\n.w3-modal{z-index:3;display:none;padding-top:100px;position:fixed;left:0;top:0;width:100%;height:100%;overflow:auto;background-color:rgb(0,0,0);background-color:rgba(0,0,0,0.4)}\r\n.w3-modal-content{margin:auto;background-color:#fff;position:relative;padding:0;outline:0;width:600px}\r\n.w3-bar{width:100%;overflow:hidden}.w3-center .w3-bar{display:inline-block;width:auto}\r\n.w3-bar .w3-bar-item{padding:8px 16px;float:left;width:auto;border:none;display:block;outline:0}\r\n.w3-bar .w3-dropdown-hover,.w3-bar .w3-dropdown-click{position:static;float:left}\r\n.w3-bar .w3-button{white-space:normal}\r\n.w3-bar-block .w3-bar-item{width:100%;display:block;padding:8px 16px;text-align:left;border:none;white-space:normal;float:none;outline:0}\r\n.w3-bar-block.w3-center .w3-bar-item{text-align:center}.w3-block{display:block;width:100%}\r\n.w3-responsive{display:block;overflow-x:auto}\r\n.w3-container:after,.w3-container:before,.w3-panel:after,.w3-panel:before,.w3-row:after,.w3-row:before,.w3-row-padding:after,.w3-row-padding:before,\r\n.w3-cell-row:before,.w3-cell-row:after,.w3-clear:after,.w3-clear:before,.w3-bar:before,.w3-bar:after{content:\"\";display:table;clear:both}\r\n.w3-col,.w3-half,.w3-third,.w3-twothird,.w3-threequarter,.w3-quarter{float:left;width:100%}\r\n.w3-col.s1{width:8.33333%}.w3-col.s2{width:16.66666%}.w3-col.s3{width:24.99999%}.w3-col.s4{width:33.33333%}\r\n.w3-col.s5{width:41.66666%}.w3-col.s6{width:49.99999%}.w3-col.s7{width:58.33333%}.w3-col.s8{width:66.66666%}\r\n.w3-col.s9{width:74.99999%}.w3-col.s10{width:83.33333%}.w3-col.s11{width:91.66666%}.w3-col.s12{width:99.99999%}\r\n@media (min-width:601px){.w3-col.m1{width:8.33333%}.w3-col.m2{width:16.66666%}.w3-col.m3,.w3-quarter{width:24.99999%}.w3-col.m4,.w3-third{width:33.33333%}\r\n.w3-col.m5{width:41.66666%}.w3-col.m6,.w3-half{width:49.99999%}.w3-col.m7{width:58.33333%}.w3-col.m8,.w3-twothird{width:66.66666%}\r\n.w3-col.m9,.w3-threequarter{width:74.99999%}.w3-col.m10{width:83.33333%}.w3-col.m11{width:91.66666%}.w3-col.m12{width:99.99999%}}\r\n@media (min-width:993px){.w3-col.l1{width:8.33333%}.w3-col.l2{width:16.66666%}.w3-col.l3{width:24.99999%}.w3-col.l4{width:33.33333%}\r\n.w3-col.l5{width:41.66666%}.w3-col.l6{width:49.99999%}.w3-col.l7{width:58.33333%}.w3-col.l8{width:66.66666%}\r\n.w3-col.l9{width:74.99999%}.w3-col.l10{width:83.33333%}.w3-col.l11{width:91.66666%}.w3-col.l12{width:99.99999%}}\r\n.w3-rest{overflow:hidden}.w3-stretch{margin-left:-16px;margin-right:-16px}\r\n.w3-content,.w3-auto{margin-left:auto;margin-right:auto}.w3-content{max-width:980px}.w3-auto{max-width:1140px}\r\n.w3-cell-row{display:table;width:100%}.w3-cell{display:table-cell}\r\n.w3-cell-top{vertical-align:top}.w3-cell-middle{vertical-align:middle}.w3-cell-bottom{vertical-align:bottom}\r\n.w3-hide{display:none!important}.w3-show-block,.w3-show{display:block!important}.w3-show-inline-block{display:inline-block!important}\r\n@media (max-width:1205px){.w3-auto{max-width:95%}}\r\n@media (max-width:600px){.w3-modal-content{margin:0 10px;width:auto!important}.w3-modal{padding-top:30px}\r\n.w3-dropdown-hover.w3-mobile .w3-dropdown-content,.w3-dropdown-click.w3-mobile .w3-dropdown-content{position:relative}\t\r\n.w3-hide-small{display:none!important}.w3-mobile{display:block;width:100%!important}.w3-bar-item.w3-mobile,.w3-dropdown-hover.w3-mobile,.w3-dropdown-click.w3-mobile{text-align:center}\r\n.w3-dropdown-hover.w3-mobile,.w3-dropdown-hover.w3-mobile .w3-btn,.w3-dropdown-hover.w3-mobile .w3-button,.w3-dropdown-click.w3-mobile,.w3-dropdown-click.w3-mobile .w3-btn,.w3-dropdown-click.w3-mobile .w3-button{width:100%}}\r\n@media (max-width:768px){.w3-modal-content{width:500px}.w3-modal{padding-top:50px}}\r\n@media (min-width:993px){.w3-modal-content{width:900px}.w3-hide-large{display:none!important}.w3-sidebar.w3-collapse{display:block!important}}\r\n@media (max-width:992px) and (min-width:601px){.w3-hide-medium{display:none!important}}\r\n@media (max-width:992px){.w3-sidebar.w3-collapse{display:none}.w3-main{margin-left:0!important;margin-right:0!important}.w3-auto{max-width:100%}}\r\n.w3-top,.w3-bottom{position:fixed;width:100%;z-index:1}.w3-top{top:0}.w3-bottom{bottom:0}\r\n.w3-overlay{position:fixed;display:none;width:100%;height:100%;top:0;left:0;right:0;bottom:0;background-color:rgba(0,0,0,0.5);z-index:2}\r\n.w3-display-topleft{position:absolute;left:0;top:0}.w3-display-topright{position:absolute;right:0;top:0}\r\n.w3-display-bottomleft{position:absolute;left:0;bottom:0}.w3-display-bottomright{position:absolute;right:0;bottom:0}\r\n.w3-display-middle{position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);-ms-transform:translate(-50%,-50%)}\r\n.w3-display-left{position:absolute;top:50%;left:0%;transform:translate(0%,-50%);-ms-transform:translate(-0%,-50%)}\r\n.w3-display-right{position:absolute;top:50%;right:0%;transform:translate(0%,-50%);-ms-transform:translate(0%,-50%)}\r\n.w3-display-topmiddle{position:absolute;left:50%;top:0;transform:translate(-50%,0%);-ms-transform:translate(-50%,0%)}\r\n.w3-display-bottommiddle{position:absolute;left:50%;bottom:0;transform:translate(-50%,0%);-ms-transform:translate(-50%,0%)}\r\n.w3-display-container:hover .w3-display-hover{display:block}.w3-display-container:hover span.w3-display-hover{display:inline-block}.w3-display-hover{display:none}\r\n.w3-display-position{position:absolute}\r\n.w3-circle{border-radius:50%}\r\n.w3-round-small{border-radius:2px}.w3-round,.w3-round-medium{border-radius:4px}.w3-round-large{border-radius:8px}.w3-round-xlarge{border-radius:16px}.w3-round-xxlarge{border-radius:32px}\r\n.w3-row-padding,.w3-row-padding>.w3-half,.w3-row-padding>.w3-third,.w3-row-padding>.w3-twothird,.w3-row-padding>.w3-threequarter,.w3-row-padding>.w3-quarter,.w3-row-padding>.w3-col{padding:0 8px}\r\n.w3-container,.w3-panel{padding:0.01em 16px}.w3-panel{margin-top:16px;margin-bottom:16px}\r\n.w3-code,.w3-codespan{font-family:Consolas,\"courier new\";font-size:16px}\r\n.w3-code{width:auto;background-color:#fff;padding:8px 12px;border-left:4px solid #4CAF50;word-wrap:break-word}\r\n.w3-codespan{color:crimson;background-color:#f1f1f1;padding-left:4px;padding-right:4px;font-size:110%}\r\n.w3-card,.w3-card-2{box-shadow:0 2px 5px 0 rgba(0,0,0,0.16),0 2px 10px 0 rgba(0,0,0,0.12)}\r\n.w3-card-4,.w3-hover-shadow:hover{box-shadow:0 4px 10px 0 rgba(0,0,0,0.2),0 4px 20px 0 rgba(0,0,0,0.19)}\r\n.w3-spin{animation:w3-spin 2s infinite linear}@keyframes w3-spin{0%{transform:rotate(0deg)}100%{transform:rotate(359deg)}}\r\n.w3-animate-fading{animation:fading 10s infinite}@keyframes fading{0%{opacity:0}50%{opacity:1}100%{opacity:0}}\r\n.w3-animate-opacity{animation:opac 0.8s}@keyframes opac{from{opacity:0} to{opacity:1}}\r\n.w3-animate-top{position:relative;animation:animatetop 0.4s}@keyframes animatetop{from{top:-300px;opacity:0} to{top:0;opacity:1}}\r\n.w3-animate-left{position:relative;animation:animateleft 0.4s}@keyframes animateleft{from{left:-300px;opacity:0} to{left:0;opacity:1}}\r\n.w3-animate-right{position:relative;animation:animateright 0.4s}@keyframes animateright{from{right:-300px;opacity:0} to{right:0;opacity:1}}\r\n.w3-animate-bottom{position:relative;animation:animatebottom 0.4s}@keyframes animatebottom{from{bottom:-300px;opacity:0} to{bottom:0;opacity:1}}\r\n.w3-animate-zoom {animation:animatezoom 0.6s}@keyframes animatezoom{from{transform:scale(0)} to{transform:scale(1)}}\r\n.w3-animate-input{transition:width 0.4s ease-in-out}.w3-animate-input:focus{width:100%!important}\r\n.w3-opacity,.w3-hover-opacity:hover{opacity:0.60}.w3-opacity-off,.w3-hover-opacity-off:hover{opacity:1}\r\n.w3-opacity-max{opacity:0.25}.w3-opacity-min{opacity:0.75}\r\n.w3-greyscale-max,.w3-grayscale-max,.w3-hover-greyscale:hover,.w3-hover-grayscale:hover{filter:grayscale(100%)}\r\n.w3-greyscale,.w3-grayscale{filter:grayscale(75%)}.w3-greyscale-min,.w3-grayscale-min{filter:grayscale(50%)}\r\n.w3-sepia{filter:sepia(75%)}.w3-sepia-max,.w3-hover-sepia:hover{filter:sepia(100%)}.w3-sepia-min{filter:sepia(50%)}\r\n.w3-tiny{font-size:10px!important}.w3-small{font-size:12px!important}.w3-medium{font-size:15px!important}.w3-large{font-size:18px!important}\r\n.w3-xlarge{font-size:24px!important}.w3-xxlarge{font-size:36px!important}.w3-xxxlarge{font-size:48px!important}.w3-jumbo{font-size:64px!important}\r\n.w3-left-align{text-align:left!important}.w3-right-align{text-align:right!important}.w3-justify{text-align:justify!important}.w3-center{text-align:center!important}\r\n.w3-border-0{border:0!important}.w3-border{border:1px solid #ccc!important}\r\n.w3-border-top{border-top:1px solid #ccc!important}.w3-border-bottom{border-bottom:1px solid #ccc!important}\r\n.w3-border-left{border-left:1px solid #ccc!important}.w3-border-right{border-right:1px solid #ccc!important}\r\n.w3-topbar{border-top:6px solid #ccc!important}.w3-bottombar{border-bottom:6px solid #ccc!important}\r\n.w3-leftbar{border-left:6px solid #ccc!important}.w3-rightbar{border-right:6px solid #ccc!important}\r\n.w3-section,.w3-code{margin-top:16px!important;margin-bottom:16px!important}\r\n.w3-margin{margin:16px!important}.w3-margin-top{margin-top:16px!important}.w3-margin-bottom{margin-bottom:16px!important}\r\n.w3-margin-left{margin-left:16px!important}.w3-margin-right{margin-right:16px!important}\r\n.w3-padding-small{padding:4px 8px!important}.w3-padding{padding:8px 16px!important}.w3-padding-large{padding:12px 24px!important}\r\n.w3-padding-16{padding-top:16px!important;padding-bottom:16px!important}.w3-padding-24{padding-top:24px!important;padding-bottom:24px!important}\r\n.w3-padding-32{padding-top:32px!important;padding-bottom:32px!important}.w3-padding-48{padding-top:48px!important;padding-bottom:48px!important}\r\n.w3-padding-64{padding-top:64px!important;padding-bottom:64px!important}\r\n.w3-left{float:left!important}.w3-right{float:right!important}\r\n.w3-button:hover{color:#000!important;background-color:#ccc!important}\r\n.w3-transparent,.w3-hover-none:hover{background-color:transparent!important}\r\n.w3-hover-none:hover{box-shadow:none!important}\r\n/* Colors */\r\n.w3-amber,.w3-hover-amber:hover{color:#000!important;background-color:#ffc107!important}\r\n.w3-aqua,.w3-hover-aqua:hover{color:#000!important;background-color:#00ffff!important}\r\n.w3-blue,.w3-hover-blue:hover{color:#fff!important;background-color:#2196F3!important}\r\n.w3-light-blue,.w3-hover-light-blue:hover{color:#000!important;background-color:#87CEEB!important}\r\n.w3-brown,.w3-hover-brown:hover{color:#fff!important;background-color:#795548!important}\r\n.w3-cyan,.w3-hover-cyan:hover{color:#000!important;background-color:#00bcd4!important}\r\n.w3-blue-grey,.w3-hover-blue-grey:hover,.w3-blue-gray,.w3-hover-blue-gray:hover{color:#fff!important;background-color:#607d8b!important}\r\n.w3-green,.w3-hover-green:hover{color:#fff!important;background-color:#4CAF50!important}\r\n.w3-light-green,.w3-hover-light-green:hover{color:#000!important;background-color:#8bc34a!important}\r\n.w3-indigo,.w3-hover-indigo:hover{color:#fff!important;background-color:#3f51b5!important}\r\n.w3-khaki,.w3-hover-khaki:hover{color:#000!important;background-color:#f0e68c!important}\r\n.w3-lime,.w3-hover-lime:hover{color:#000!important;background-color:#cddc39!important}\r\n.w3-orange,.w3-hover-orange:hover{color:#000!important;background-color:#ff9800!important}\r\n.w3-deep-orange,.w3-hover-deep-orange:hover{color:#fff!important;background-color:#ff5722!important}\r\n.w3-pink,.w3-hover-pink:hover{color:#fff!important;background-color:#e91e63!important}\r\n.w3-purple,.w3-hover-purple:hover{color:#fff!important;background-color:#9c27b0!important}\r\n.w3-deep-purple,.w3-hover-deep-purple:hover{color:#fff!important;background-color:#673ab7!important}\r\n.w3-red,.w3-hover-red:hover{color:#fff!important;background-color:#f44336!important}\r\n.w3-sand,.w3-hover-sand:hover{color:#000!important;background-color:#fdf5e6!important}\r\n.w3-teal,.w3-hover-teal:hover{color:#fff!important;background-color:#009688!important}\r\n.w3-yellow,.w3-hover-yellow:hover{color:#000!important;background-color:#ffeb3b!important}\r\n.w3-white,.w3-hover-white:hover{color:#000!important;background-color:#fff!important}\r\n.w3-black,.w3-hover-black:hover{color:#fff!important;background-color:#000!important}\r\n.w3-grey,.w3-hover-grey:hover,.w3-gray,.w3-hover-gray:hover{color:#000!important;background-color:#9e9e9e!important}\r\n.w3-light-grey,.w3-hover-light-grey:hover,.w3-light-gray,.w3-hover-light-gray:hover{color:#000!important;background-color:#f1f1f1!important}\r\n.w3-dark-grey,.w3-hover-dark-grey:hover,.w3-dark-gray,.w3-hover-dark-gray:hover{color:#fff!important;background-color:#616161!important}\r\n.w3-pale-red,.w3-hover-pale-red:hover{color:#000!important;background-color:#ffdddd!important}\r\n.w3-pale-green,.w3-hover-pale-green:hover{color:#000!important;background-color:#ddffdd!important}\r\n.w3-pale-yellow,.w3-hover-pale-yellow:hover{color:#000!important;background-color:#ffffcc!important}\r\n.w3-pale-blue,.w3-hover-pale-blue:hover{color:#000!important;background-color:#ddffff!important}\r\n.w3-text-amber,.w3-hover-text-amber:hover{color:#ffc107!important}\r\n.w3-text-aqua,.w3-hover-text-aqua:hover{color:#00ffff!important}\r\n.w3-text-blue,.w3-hover-text-blue:hover{color:#2196F3!important}\r\n.w3-text-light-blue,.w3-hover-text-light-blue:hover{color:#87CEEB!important}\r\n.w3-text-brown,.w3-hover-text-brown:hover{color:#795548!important}\r\n.w3-text-cyan,.w3-hover-text-cyan:hover{color:#00bcd4!important}\r\n.w3-text-blue-grey,.w3-hover-text-blue-grey:hover,.w3-text-blue-gray,.w3-hover-text-blue-gray:hover{color:#607d8b!important}\r\n.w3-text-green,.w3-hover-text-green:hover{color:#4CAF50!important}\r\n.w3-text-light-green,.w3-hover-text-light-green:hover{color:#8bc34a!important}\r\n.w3-text-indigo,.w3-hover-text-indigo:hover{color:#3f51b5!important}\r\n.w3-text-khaki,.w3-hover-text-khaki:hover{color:#b4aa50!important}\r\n.w3-text-lime,.w3-hover-text-lime:hover{color:#cddc39!important}\r\n.w3-text-orange,.w3-hover-text-orange:hover{color:#ff9800!important}\r\n.w3-text-deep-orange,.w3-hover-text-deep-orange:hover{color:#ff5722!important}\r\n.w3-text-pink,.w3-hover-text-pink:hover{color:#e91e63!important}\r\n.w3-text-purple,.w3-hover-text-purple:hover{color:#9c27b0!important}\r\n.w3-text-deep-purple,.w3-hover-text-deep-purple:hover{color:#673ab7!important}\r\n.w3-text-red,.w3-hover-text-red:hover{color:#f44336!important}\r\n.w3-text-sand,.w3-hover-text-sand:hover{color:#fdf5e6!important}\r\n.w3-text-teal,.w3-hover-text-teal:hover{color:#009688!important}\r\n.w3-text-yellow,.w3-hover-text-yellow:hover{color:#d2be0e!important}\r\n.w3-text-white,.w3-hover-text-white:hover{color:#fff!important}\r\n.w3-text-black,.w3-hover-text-black:hover{color:#000!important}\r\n.w3-text-grey,.w3-hover-text-grey:hover,.w3-text-gray,.w3-hover-text-gray:hover{color:#757575!important}\r\n.w3-text-light-grey,.w3-hover-text-light-grey:hover,.w3-text-light-gray,.w3-hover-text-light-gray:hover{color:#f1f1f1!important}\r\n.w3-text-dark-grey,.w3-hover-text-dark-grey:hover,.w3-text-dark-gray,.w3-hover-text-dark-gray:hover{color:#3a3a3a!important}\r\n.w3-border-amber,.w3-hover-border-amber:hover{border-color:#ffc107!important}\r\n.w3-border-aqua,.w3-hover-border-aqua:hover{border-color:#00ffff!important}\r\n.w3-border-blue,.w3-hover-border-blue:hover{border-color:#2196F3!important}\r\n.w3-border-light-blue,.w3-hover-border-light-blue:hover{border-color:#87CEEB!important}\r\n.w3-border-brown,.w3-hover-border-brown:hover{border-color:#795548!important}\r\n.w3-border-cyan,.w3-hover-border-cyan:hover{border-color:#00bcd4!important}\r\n.w3-border-blue-grey,.w3-hover-border-blue-grey:hover,.w3-border-blue-gray,.w3-hover-border-blue-gray:hover{border-color:#607d8b!important}\r\n.w3-border-green,.w3-hover-border-green:hover{border-color:#4CAF50!important}\r\n.w3-border-light-green,.w3-hover-border-light-green:hover{border-color:#8bc34a!important}\r\n.w3-border-indigo,.w3-hover-border-indigo:hover{border-color:#3f51b5!important}\r\n.w3-border-khaki,.w3-hover-border-khaki:hover{border-color:#f0e68c!important}\r\n.w3-border-lime,.w3-hover-border-lime:hover{border-color:#cddc39!important}\r\n.w3-border-orange,.w3-hover-border-orange:hover{border-color:#ff9800!important}\r\n.w3-border-deep-orange,.w3-hover-border-deep-orange:hover{border-color:#ff5722!important}\r\n.w3-border-pink,.w3-hover-border-pink:hover{border-color:#e91e63!important}\r\n.w3-border-purple,.w3-hover-border-purple:hover{border-color:#9c27b0!important}\r\n.w3-border-deep-purple,.w3-hover-border-deep-purple:hover{border-color:#673ab7!important}\r\n.w3-border-red,.w3-hover-border-red:hover{border-color:#f44336!important}\r\n.w3-border-sand,.w3-hover-border-sand:hover{border-color:#fdf5e6!important}\r\n.w3-border-teal,.w3-hover-border-teal:hover{border-color:#009688!important}\r\n.w3-border-yellow,.w3-hover-border-yellow:hover{border-color:#ffeb3b!important}\r\n.w3-border-white,.w3-hover-border-white:hover{border-color:#fff!important}\r\n.w3-border-black,.w3-hover-border-black:hover{border-color:#000!important}\r\n.w3-border-grey,.w3-hover-border-grey:hover,.w3-border-gray,.w3-hover-border-gray:hover{border-color:#9e9e9e!important}\r\n.w3-border-light-grey,.w3-hover-border-light-grey:hover,.w3-border-light-gray,.w3-hover-border-light-gray:hover{border-color:#f1f1f1!important}\r\n.w3-border-dark-grey,.w3-hover-border-dark-grey:hover,.w3-border-dark-gray,.w3-hover-border-dark-gray:hover{border-color:#616161!important}\r\n.w3-border-pale-red,.w3-hover-border-pale-red:hover{border-color:#ffe7e7!important}.w3-border-pale-green,.w3-hover-border-pale-green:hover{border-color:#e7ffe7!important}\r\n.w3-border-pale-yellow,.w3-hover-border-pale-yellow:hover{border-color:#ffffcc!important}.w3-border-pale-blue,.w3-hover-border-pale-blue:hover{border-color:#e7ffff!important}\r\n</style>\r\n</head>\r\n\r\n<body>\r\n\r\n<div class=\"w3-container\">\r\n  <h2>JSchemaTool</h2>\r\n  <p>Highly useful even if not perfect. Also it's free.</p>\r\n  <p>Supported fetures:</p>\r\n  <ul class=\"w3-ul w3-green w3-small\">\r\n  <li>Generate empty JSON payload from JSON schema.</li>\r\n  <li>Generate JSON schema from JSON payload.</li>\r\n  <li>Edit schema using GUI editor.</li>\r\n  <li>Create new schema.</li>\r\n</ul>\r\n  <p>Unsupported:</p>\r\n  <ul class=\"w3-ul w3-red w3-small\">\r\n  <li>Defenitions</li>\r\n  <li>$ref</li>\r\n  <li>Required</li>\r\n</ul>\r\n\r\n</div>\r\n\r\n\r\n</body>\r\n</html>");

	}
	String jsonWorkspace="";
	public String identifier="-~-~-~-010-~-~-~";
	private void appendln(String data) {
		jsonWorkspace+=data+"\n";
		//System.out.println(data.replaceAll(","+identifier, "").replaceAll(identifier, ""));
	}
	
	private void append(String data) {
		jsonWorkspace+=data;
		//System.out.print(data.replaceAll(","+identifier, "").replaceAll(identifier, ""));
	}
	private void newSchema(int pad) throws Exception {
		int count=tree.getItemCount();
		TreeItem items[]=tree.getItems();
		appendln("{");
		for (TreeItem item : items) {
			appendln(ImportJSON.padding(pad)+"\""+item.getText(nameIndex)+"\":{");
			appendln(ImportJSON.padding(pad+1)+"\"type\":\"object\",");
    		appendln(ImportJSON.padding(pad+1)+"\"title\":\""+item.getText(titleIndex)+"\",");
    		appendln(ImportJSON.padding(pad+1)+"\"description\":\""+item.getText(descIndex)+"\",");
    		appendln(ImportJSON.padding(pad+1)+"\"properties\":{");
			newSchema(item,pad+1);
			appendln(ImportJSON.padding(pad+1)+"}");
    		append(ImportJSON.padding(pad)+"},");
		}
		appendln(identifier);
		appendln("}");
	}
	private void newSchema(TreeItem treeItem,int pad) throws Exception{
		//int count=treeItem.getItemCount();
		TreeItem items[]=items=treeItem.getItems();
		
		for (TreeItem item : items) {
			String type=item.getText(typeIndex);//type of object
			if(type.equalsIgnoreCase("object")) {
				appendln("\n"+ImportJSON.padding(pad)+"\""+item.getText(nameIndex)+"\":{");
	    		appendln(ImportJSON.padding(pad+1)+"\"type\":\"object\",");
	    		appendln(ImportJSON.padding(pad+1)+"\"title\":\""+item.getText(titleIndex)+"\",");
	    		appendln(ImportJSON.padding(pad+1)+"\"description\":\""+item.getText(descIndex)+"\",");
	    		appendln(ImportJSON.padding(pad+1)+"\"properties\":{");
	    		newSchema(item, pad+2);
	    		appendln(ImportJSON.padding(pad+1)+"}");
	    		append(ImportJSON.padding(pad)+"},");
			}
			else if(type.equalsIgnoreCase("array")) {
				appendln("\n"+ImportJSON.padding(pad)+"\""+item.getText(nameIndex)+"\":{");
				appendln(ImportJSON.padding(pad+1)+"\"type\":\"array\",");
				appendln(ImportJSON.padding(pad+1)+"\"title\":\""+item.getText(titleIndex)+"\",");
				appendln(ImportJSON.padding(pad+1)+"\"description\":\""+item.getText(descIndex)+"\",");
				appendln(ImportJSON.padding(pad+1)+"\"items\":{");
				appendln(ImportJSON.padding(pad+2)+"\"type\":\"object\",");
				appendln(ImportJSON.padding(pad+2)+"\"properties\":{");
				newSchema(item, pad+3);
				appendln(ImportJSON.padding(pad+2)+"}");
				appendln(ImportJSON.padding(pad+1)+"}");
				append(ImportJSON.padding(pad)+"},");
			}
			else {
				appendln("\n"+ImportJSON.padding(pad)+"\""+item.getText(nameIndex)+"\":{");
	    		append(ImportJSON.padding(pad+1)+"\"type\":\""+item.getText(typeIndex)+"\",");
	    		if(item.getText(titleIndex)!=null && item.getText(titleIndex).trim().length()>0)
	    			append(ImportJSON.padding(pad+1)+"\n\"title\":\""+item.getText(titleIndex)+"\",");
	    		if(item.getText(descIndex)!=null && item.getText(descIndex).trim().length()>0)
	    			append(ImportJSON.padding(pad+1)+"\n\"description\":\""+item.getText(descIndex)+"\",");
	    		if(item.getText(formatIndex)!=null && item.getText(formatIndex).trim().length()>0)
	    			append(ImportJSON.padding(pad+1)+"\n\"format\":\""+item.getText(formatIndex)+"\",");
	    		if(item.getText(egIndex)!=null && item.getText(egIndex).trim().length()>0)
	    			append(ImportJSON.padding(pad+1)+"\n\"example\":\""+item.getText(egIndex)+"\",");
	    		if(item.getText(enumIndex)!=null && item.getText(enumIndex).trim().length()>0)
	    			append(ImportJSON.padding(pad+1)+"\n\"enum\":["+item.getText(enumIndex)+"],");
	    		appendln(identifier);
	    		append(ImportJSON.padding(pad)+"},");
			}
		}
		appendln(identifier);
	}
	private void loadSchemaEditor(Map<String, Object> map, Tree tree) throws Exception{
		Object[] keys=map.keySet().toArray();
	    int itemCount = keys.length;
	    for (int i = 0; i < itemCount; i++) {
		    TreeItem item = new TreeItem(tree, SWT.FULL_SELECTION);
		    item.setText(nameIndex,keys[i].toString());
		    //item.getCl
		    if(map.get(keys[i]) instanceof Map)
		    loadSchemaEditor((Map)map.get(keys[i]), item);
	    }
	}
	
	private void loadSchemaEditor(Map<String, Object> map, TreeItem ti) throws Exception{
		Object[] keys=map.keySet().toArray();
	    int itemCount = keys.length;
	    ti.setText(descIndex, (map.get("description")+"").replace("null", ""));
	    ti.setText(titleIndex, (map.get("title")+"").replace("null", ""));
	    ti.setText(typeIndex, (map.get("type")+"").replace("null", ""));
	    ti.setText(egIndex, (map.get("example")+"").replace("null", ""));
	    ti.setText(formatIndex, (map.get("format")+"").replace("null", ""));
	    ti.setText(enumIndex, (map.get("enum")+"").replace("null", ""));
	    for (int i = 0; i < itemCount; i++) {
	    	if(map.get(keys[i]) instanceof Map) {
		    	TreeItem item = new TreeItem(ti, SWT.FULL_SELECTION);
		    	item.setText(0,keys[i].toString());
		    	loadSchemaEditor((Map) map.get(keys[i]), item);
	    	}
	    }
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private String openFileDialog() throws Exception{
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Open");
		String[] filterExt = { "*.json;*.JSON" };
		String[] filterNames = { "TXT files" };
		fd.setFilterExtensions(filterExt);
		fd.setFilterNames(filterNames);
		/*String lastPath = System.getInstance().getString(Config.LAST_OPEN_TEXT_PATH);
		if (lastPath != null && !lastPath.isEmpty())
			fd.setFileName(lastPath);
		*/
		String selected = fd.open();
		return selected;
	}
}
