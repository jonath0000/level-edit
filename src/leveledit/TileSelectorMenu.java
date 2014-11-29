package leveledit;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;

import tools.TileSelector;

/**
 * Frame allowing user to choose a tile.
 * 
 */
public class TileSelectorMenu extends JScrollPane implements TileSelector {

	public JList tileList;

	public TileSelectorMenu() {
		super();
		tileList = new JList();
		this.setViewportView(tileList);
	}

	public void setTiles(Image tiles, int nTiles, int tilesPerRowInImage, int sourceWidth, int sourceHeight, int representationWidth, int representationHeight) {

		ImageIcon listitems[] = new ImageIcon[nTiles];

		for (int i = 0; i < nTiles; i++) {

			int row = i / tilesPerRowInImage;
			int tile = i % tilesPerRowInImage;

			BufferedImage newIm = new BufferedImage(representationWidth, representationHeight, BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = newIm.getGraphics();
			g.drawImage(tiles, 0, 0, representationWidth, representationHeight, tile * sourceWidth, row * sourceHeight, (tile + 1)
					* sourceWidth, (row + 1) * sourceHeight, this);
			ImageIcon icon = new ImageIcon(newIm);
			listitems[i] = icon;
		}

		tileList.setLayoutOrientation(JList.VERTICAL_WRAP);
		tileList.setVisibleRowCount(8);
		//setPreferredSize(new Dimension(representationWidth * 8, representationHeight * 8));
		tileList.setListData(listitems);
	}

	@Override
	public int getSelectedIndex() {
		return tileList.getSelectedIndex() + 1;
	}

	@Override
	public void setSelectedIndex(int n) {
		if (n <= 0) return;
		tileList.setSelectedIndex(n - 1);
	}
}
