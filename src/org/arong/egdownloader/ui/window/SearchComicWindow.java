package org.arong.egdownloader.ui.window;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.arong.egdownloader.model.SearchTask;
import org.arong.egdownloader.ui.ComponentConst;
import org.arong.egdownloader.ui.ComponentUtil;
import org.arong.egdownloader.ui.swing.AJButton;
import org.arong.egdownloader.ui.swing.AJLabel;
import org.arong.egdownloader.ui.swing.AJPager;
import org.arong.egdownloader.ui.swing.AJTextField;
import org.arong.egdownloader.ui.table.SearchTasksTable;
import org.arong.egdownloader.ui.work.SearchComicWorker;

/**
 * 显示绅士站漫画列表窗口
 * @author dipoo
 * @since 2015-03-11
 */
public class SearchComicWindow extends JFrame {

	private static final long serialVersionUID = -3912589805632312855L;
	private EgDownloaderWindow mainWindow;
	private JTextField keyField;
	private JLabel loadingLabel;
	public JLabel totalLabel;
	private JButton searchBtn;
	public AJPager pager;
	public String key = " ";
	public List<SearchTask> searchTasks = new ArrayList<SearchTask>();
	public Map<String, Map<String, List<SearchTask>>> datas = new HashMap<String, Map<String, List<SearchTask>>>();
	public SearchComicWindow(final EgDownloaderWindow mainWindow){
		this.mainWindow = mainWindow;
		this.setSize(ComponentConst.CLIENT_WIDTH, ComponentConst.CLIENT_HEIGHT);
		this.setTitle("搜索里站漫画");
		this.setLayout(null);
		this.setResizable(false);
		this.setLocationRelativeTo(null);  
		JLabel keyLabel = new AJLabel("关键字", Color.BLUE, 20, 20, 50, 30);
		keyField = new AJTextField("", 60, 20, 440, 30);
		keyField.setText("chinese big");
		
		loadingLabel = new AJLabel("正在加载数据", "loading.gif", Color.BLACK, JLabel.LEFT);
		loadingLabel.setBounds(600, 20, 120, 30);
		loadingLabel.setVisible(false);
		
		totalLabel = new AJLabel("", null, Color.BLACK, JLabel.LEFT);
		totalLabel.setBounds(600, 20, 300, 30);
		totalLabel.setVisible(false);
		
		pager = new AJPager(20, ComponentConst.CLIENT_HEIGHT - 80, ComponentConst.CLIENT_WIDTH, ComponentConst.CLIENT_HEIGHT, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JButton btn = (JButton) e.getSource();
				search((Integer.parseInt(btn.getName()) - 1) + "");
			}
		});
		pager.setVisible(false);
		
		searchBtn = new AJButton("搜索", "", new ActionListener() {
			
			public void actionPerformed(ActionEvent ae) {
				search("0");
			}
			
		}, 510, 20, 60, 30);
		
		ComponentUtil.addComponents(this.getContentPane(), keyLabel, keyField, searchBtn, loadingLabel, totalLabel, pager);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//关闭后显示主界面
				mainWindow.setVisible(true);
				mainWindow.setEnabled(true);
				JFrame w = (JFrame)e.getSource();
				w.dispose();
			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				SearchComicWindow this_ = (SearchComicWindow) e.getSource();
				this_.dispose();
			}
		});
	}
	
	public void search(String page){
		showLoading();
		String k = keyField.getText().trim();
		if(datas.containsKey(k) && datas.get(k).containsKey(page)){
			hideLoading();
		}else{
			key = k;
			//过滤key
			k = filterUrl(k);
			String exurl = "http://exhentai.org/?page=" + page + "&f_apply=Apply+Filter&f_doujinshi=1&f_manga=1&f_artistcg=1&f_gamecg=1&f_western=1&f_non-h=1&f_imageset=1" + 
			"&f_cosplay=1&f_asianporn=1&f_misc=1&f_search=" + k;
			new SearchComicWorker(mainWindow, exurl, Integer.parseInt(page) + 1).execute();
		}
	}
	
	public String filterUrl(String url){
		if(url != null){
			return url.replaceAll("\\%", "%25")
			.replaceAll("\\+", "%2B")
			.replaceAll(" ", "+")
			.replaceAll("\\/", "%2F")
			.replaceAll("\\?", "%3F")
			.replaceAll("\\#", "%23")
			.replaceAll("\\&", "%26")
			.replaceAll("\\=", "%3D");
		}
		return null;
	}
	
	public void showResult(String totalPage, Integer currentPage){
		if(mainWindow.searchTable == null){
			mainWindow.searchTable = new SearchTasksTable(5, 70, ComponentConst.CLIENT_WIDTH - 20,
					ComponentConst.CLIENT_HEIGHT - 150, searchTasks, mainWindow);
			JScrollPane tablePane = new JScrollPane(mainWindow.searchTable);
			tablePane.setBounds(5, 70, ComponentConst.CLIENT_WIDTH - 20, ComponentConst.CLIENT_HEIGHT - 150);
			tablePane.getViewport().setBackground(new Color(254,254,254));
			mainWindow.searchComicWindow.getContentPane().add(tablePane);
		}
		mainWindow.searchTable.setVisible(true);
		mainWindow.searchTable.updateUI();
		if(totalPage != null && currentPage != null){
			mainWindow.searchComicWindow.pager.change(Integer.parseInt(totalPage), currentPage);
			mainWindow.searchComicWindow.pager.setVisible(true);
		}
	}
	
	public void showLoading(){
		totalLabel.setVisible(false);
		loadingLabel.setVisible(true);
		searchBtn.setEnabled(false);
	}
	
	public void hideLoading(){
		loadingLabel.setVisible(false);
		searchBtn.setEnabled(true);
		totalLabel.setVisible(true);
	}
	
	public void setTotalInfo(String totalPage, String totalTasks){
		totalLabel.setText("共搜索到 " + totalPage + " 页,总计 " + totalTasks + " 本漫画");
	}
	
	public void dispose() {
		mainWindow.setEnabled(true);
		mainWindow.setVisible(true);
		super.dispose();
	}
	
}
