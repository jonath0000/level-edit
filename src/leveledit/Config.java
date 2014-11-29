package leveledit;

import levelmodel.DummyObject;
import graphicsutils.CompatibleImageCreator;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Represents the current project configuration from a file.
 * 
 */
public class Config {

	// Values read from file.
	public int numTiles;
	public Image tiles;
	public Color bgCol;
	public ArrayList<DummyObject> dummyDefinitions;
	public int sourceImageTileSize = 16;
	public int representationTileSize = 32;
	public int tilesPerRow = 8;
	public int mirrorTileVal = 100;
	public String projectPath = ".";
	public String exportPath = ".";
	public String dummyImagePath = ".";

	public Config(String path, String tileMapImageOverride) {
		System.out.println("Loading config file " + path);
		
		if (path.endsWith(".xml")) {
			readFromXml(path, tileMapImageOverride);
		} else {
			System.out.println("Config file not xml. Ignoring.");
		}
	}
	
	private String getTextValueOfElement(String defaultValue, Element doc, String tag) {
		String value = defaultValue;
	    NodeList nl;
	    nl = doc.getElementsByTagName(tag);
	    if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
	        value = nl.item(0).getFirstChild().getNodeValue();
	    } else {
	    	System.out.println("Couldn't find tag <" + tag + "> , using default value \"" + defaultValue + "\"");
	    }
	    return value;
	}

	public void readFromXml(String path, String tileMapImageOverride) {
		Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(path);
            Element doc = dom.getDocumentElement();
            projectPath = getTextValueOfElement(projectPath, doc, "projectPath");
            exportPath = getTextValueOfElement(exportPath, doc, "exportPath");
            bgCol = new Color(Integer.parseInt(getTextValueOfElement("AABBCC", doc, "editorBackgroundColor"), 16));
            
            // tiles
            String tileMapImagePath = getTextValueOfElement(null, doc, "tileMapImagePath");
            if (tileMapImageOverride != null && tileMapImageOverride.length() > 0) {
            	tileMapImagePath = tileMapImageOverride;
			}
			tiles = CompatibleImageCreator.createCompatibleImageFromFile(tileMapImagePath);
            sourceImageTileSize = Integer.parseInt(getTextValueOfElement("16", doc, "tileSize"));
            tilesPerRow = Integer.parseInt(getTextValueOfElement("8", doc, "tilesPerRow"));
            mirrorTileVal = Integer.parseInt(getTextValueOfElement("300", doc, "tileMirrorIndex"));
            numTiles = (tiles.getWidth(null) * tiles.getHeight(null)) / (sourceImageTileSize * sourceImageTileSize);
            
            // dummys
            dummyDefinitions = new ArrayList<DummyObject>();
            dummyImagePath = getTextValueOfElement(dummyImagePath, doc, "dummyImagePath");
            
            NodeList dummyDefinitionElements;
            dummyDefinitionElements = doc.getElementsByTagName("dummyDefinition");
    	    for (int i = 0; i < dummyDefinitionElements.getLength(); i++) {
    	    	
    	    	Node dummyDef = dummyDefinitionElements.item(i);
    	    	NamedNodeMap attributes = dummyDef.getAttributes();
    	    	String type = null;
	    		int width = -1;
	    		int height = -1;
	    		String otherAttributes = "";
	    		
    	    	for (int j = 0; j < attributes.getLength(); j++) {
    	    		String attribute = attributes.item(j).getNodeName();
    	    		String value = attributes.item(j).getNodeValue();
    	    		
    	    		if (attribute.equals("type")) {
    	    			type = value;
    	    		} else if (attribute.equals("width")) {
    	    			width = Integer.parseInt(value);
    	    		} else if (attribute.equals("height")) {
    	    			height = Integer.parseInt(value);
    	    		} else {
    	    			// TODO best way to implement additional data ?
    	    			if (otherAttributes.isEmpty()) {
    	    				otherAttributes += value;
    	    			} else {
    	    				otherAttributes += "," + value;
    	    			}
    	    		}
    	    	}
    	    	DummyObject dummy = new DummyObject(0, 0, width, height, otherAttributes, type);
				dummyDefinitions.add(dummy);
    	    }
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
	}
}
