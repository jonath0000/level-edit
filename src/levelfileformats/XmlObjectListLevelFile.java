package levelfileformats;

import java.io.PrintWriter;

import levelmodel.DummyObject;
import levelmodel.DummyObjectList;
import levelmodel.LevelFileInterface;
import levelmodel.TileMap;

/**
 * Export object list in XML.
 */
public class XmlObjectListLevelFile 
implements LevelFileInterface {
    
    private String path;
    
    /**
     * Is created for specific file.
     */
    public XmlObjectListLevelFile(String path) {
        this.path = path;
    }
    
    /**
     * Write level to file.
     * 
     * @return true if ok.
     */
    @Override
    public boolean write(DummyObjectList dummyObjects, TileMap tileMap) {
    	try {
			PrintWriter out = new PrintWriter(path);
			out.println("<objectlist>");
			out.println("	<objects>");
			
			for (int i = 0; i < dummyObjects.size(); i++) {
				DummyObject d = dummyObjects.elementAt(i);
				out.print("		<object ");
				out.print("type=\""+ d.name + "\" ");
				out.print("x=\""+ d.x + "\" "); 
				out.print("y=\""+ d.y + "\" ");
				if (d.additionalData.length() > 0) {
					out.print("data=\""+ d.additionalData + "\" ");
				}
				out.println("/>");
			}

			out.println("	</objects>");
			out.println("</objectlist>");
			out.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		return true;
    }
    
    /**
     * Not supported.
     * @return false.
     */
    @Override
    public boolean read(DummyObjectList dummyObjects, DummyObject [] dummyTypes, 
        TileMap tileMap) {
        return false;
    }   
}