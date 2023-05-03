import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import java.awt.Image;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.BasicStroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class Driver {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        final Image stopImage;
        final Image tripPointImage;
        try {
            stopImage = ImageIO.read(new File("stopsign.png"));
            tripPointImage = ImageIO.read(new File("raccoon.png"));
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
            return;
        }

        // Read file and call stop detection
        TripPoint.readFile("triplog.csv");
        TripPoint.h1StopDetection();

        // Set up frame, include your name in the title
        JFrame frame = new JFrame("Trip Analyzer - Khoi Le");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        // Initialize JMapViewer and add it to your JFrame
        JMapViewer mapViewer = new JMapViewer();
        mapViewer.setTileSource(new OsmTileSource.TransportMap());
        frame.add(mapViewer, BorderLayout.CENTER);

        // Set up Panel for input selections
        JPanel inputPanel = new JPanel(new FlowLayout());

        // Play Button
        JButton playButton = new JButton("Play");
        inputPanel.add(playButton);

        // CheckBox to enable/disable stops
        JCheckBox enableStopsCheckBox = new JCheckBox("Enable Stops");
        inputPanel.add(enableStopsCheckBox);
        



        // ComboBox to pick animation time
        JComboBox<String> animationTimeComboBox = new JComboBox<>(new String[]{"15s", "30s", "60s","90s"});
        inputPanel.add(animationTimeComboBox);
        
     // Add ItemListener for enableStopsCheckBox
        enableStopsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean showStops = e.getStateChange() == ItemEvent.SELECTED;
                for (MapMarker stopMarker : mapViewer.getMapMarkerList()) {
                    if (stopMarker instanceof IconMarker && ((IconMarker) stopMarker).getImage() == stopImage) {
                        ((IconMarker) stopMarker).setVisible(showStops);
                    }
                }
                mapViewer.repaint();
            }
        });

        // Add all to top panel
        frame.add(inputPanel, BorderLayout.NORTH);

        // Add listeners for GUI components
        playButton.addActionListener(new ActionListener() {
            private Timer timer;
            private ArrayList<TripPoint> tripPoints = TripPoint.getMovingTrip();
            private int animationTime = 15000;
            private boolean isPlaying = false;
            private IconMarker movingMarker;
            private MapPolygonImpl polyline;
            private List<Coordinate> pathTaken = new ArrayList<>(); // Add this line to store the path taken

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    timer.stop();
                    movingMarker.setVisible(false);
                    if (polyline != null) {
                        mapViewer.removeMapPolygon(polyline);
                    }
                    isPlaying = false;
                } else {
                    pathTaken.clear();

                    // Get the selected animation time from the combo box
                    String selectedTime = (String) animationTimeComboBox.getSelectedItem();
                    switch (selectedTime) {
                        case "15s":
                            animationTime = 15000;
                            break;
                        case "30s":
                            animationTime = 30000;
                            break;
                        case "60s":
                            animationTime = 60000;
                            break;
                        case "90s":
                            animationTime = 90000;
                            break;
                        default:
                            animationTime = 15000;
                    }

                    enableStopsCheckBox.isSelected();

                    // Create a single marker for the moving image
                    Coordinate coord = new Coordinate(tripPoints.get(0).getLat(), tripPoints.get(0).getLon());
                    movingMarker = new IconMarker(coord, tripPointImage);
                    mapViewer.addMapMarker(movingMarker);

                    // Create a MapPolyline from the trip points
                    List<Coordinate> polylineCoordinates = tripPoints.stream()
                            .map(tp -> new Coordinate(tp.getLat(), tp.getLon()))
                            .collect(Collectors.toList());

                    // Start the animation
                    movingMarker.setVisible(true);
                    timer = new Timer(animationTime / polylineCoordinates.size(), new ActionListener() {
                        private int polylineIndex = 0;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Move to the next polyline coordinate
                            polylineIndex++;

                            // Wrap around to the beginning if we've reached the end
                            if (polylineIndex >= polylineCoordinates.size()) {
                                polylineIndex = 0;
                                pathTaken.clear(); // Clear the path taken when the animation restarts
                            }

                            // Get the next coordinate for the moving marker
                            Coordinate newPosition = polylineCoordinates.get(polylineIndex);
                            movingMarker.setCoordinate(newPosition);

                            pathTaken.add(newPosition); // Add the new position to the path taken

                            // Remove the previous polyline from the map viewer
                            if (polyline != null) {
                                mapViewer.removeMapPolygon(polyline);
                            }

                            // Create a new polyline for the path taken and add it to the map viewer
                            polyline = new CustomMapPolyline(new ArrayList<>(pathTaken), new BasicStroke(2));
                            mapViewer.addMapPolygon(polyline);

                            // Check if the moving marker has passed a stop marker
                         // Check if the moving marker has passed a stop marker
                          
                            

                            // Set the display position of the map viewer to the current position of the moving marker
                            mapViewer.setDisplayPosition(newPosition, 6);

                            // Redraw the map
                            mapViewer.repaint();
                        }
                    });
                    timer.start();
                    isPlaying = true;
                }
            }
        });

        // Add markers for the trip points and stops
        ArrayList<TripPoint> movingTripPoints = TripPoint.getMovingTrip();

        for (TripPoint tp : movingTripPoints) {
            Coordinate coord = new Coordinate(tp.getLat(), tp.getLon());
            IconMarker marker;
            if (TripPoint.getTrip().contains(tp)) {
                marker = new IconMarker(coord, stopImage);
                marker.setVisible(false);
            } else {
                marker = new IconMarker(coord, tripPointImage);
            }
            mapViewer.addMapMarker(marker);
        }
        Coordinate center = new Coordinate(movingTripPoints.get(0).getLat(), movingTripPoints.get(0).getLon());
        int zoomLevel = 12;
        mapViewer.setDisplayPosition(center, zoomLevel);

        // Display the frame
        frame.setVisible(true);
    }
}
