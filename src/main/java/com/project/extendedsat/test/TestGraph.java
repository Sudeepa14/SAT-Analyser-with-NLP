package com.project.extendedsat.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import javax.swing.JFrame;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.*;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import processing.core.PApplet;

/**
 *
 *
 * @author SAMITHA
 */
public class TestGraph  {

	public void script() {
		//Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		//Import file
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		Container container;
		try {
			File file = new File("SourceToTest.gexf");
			container = importController.importFile(file);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		//Preview configuration
		PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
		PreviewModel previewModel = previewController.getModel();

		previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.RED));
		previewModel.getProperties().putValue(PreviewProperty.EDGE_LABEL_COLOR, new DependantOriginalColor(Color.GREEN));
		// previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_COLOR,new DependantColor(DependantColor.Mode.PARENT));
		//previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new DependantOriginalColor(Color.GREEN));
		previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 0f);
		previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
		previewController.refreshPreview();

		//New Processing target, get the PApplet
		ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
		PApplet applet = target.getApplet();
		applet.init();

		//Refresh the preview and reset the zoom
		previewController.render(target);
		target.refresh();
		target.resetZoom();

		//Add the applet to a JFrame and display
		JFrame frame = new JFrame("Test Preview");
		frame.setLayout(new BorderLayout());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(applet, BorderLayout.CENTER);
		frame.setSize(1650,1080);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.pack();
		frame.setVisible(true);

	}

}