import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.List;

public class CustomMapPolyline extends MapPolygonImpl {
    private Stroke stroke;

    public CustomMapPolyline(List<? extends ICoordinate> points, Stroke stroke) {
        super(null, null, points);
        this.stroke = stroke;
    }

    @Override
    public void paint(Graphics g, List<Point> points) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(stroke);
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        g2d.dispose();
    }
}
