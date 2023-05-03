import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import java.awt.*;
import java.util.List;

public class MapPolyline implements MapObject {

    private final String name;
    private final List<Coordinate> coordinates;
    private final Color color;
    private boolean visible = true;

    public MapPolyline(String name, Color color, List<Coordinate> coordinates) {
        this.name = name;
        this.coordinates = coordinates;
        this.color = color;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Color getBackColor() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Layer getLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stroke getStroke() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Style getStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Style getStyleAssigned() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLayer(Layer arg0) {
		// TODO Auto-generated method stub
		
	}
}
