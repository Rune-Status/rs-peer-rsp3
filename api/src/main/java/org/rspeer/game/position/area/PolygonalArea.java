package org.rspeer.game.position.area;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.position.Position;

public class PolygonalArea implements Area {

    private final int floorLevel;
    private final Map<Integer, Position> ps;

    PolygonalArea(Position... edges) {
        int[] xPoints = new int[edges.length];
        int[] yPoints = new int[edges.length];

        for (int i = 0; i < edges.length; i++) {
            Position position = edges[i];
            xPoints[i] = position.getX();
            yPoints[i] = position.getY();
        }

        this.ps = new HashMap<>();
        this.floorLevel = edges.length > 0 ? edges[0].getFloorLevel() : 0;

        Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
        Rectangle bounds = polygon.getBounds();

        for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
            for (int y = bounds.y; y < bounds.y + bounds.height; y++) {

                if (polygon.contains(x, y)) {
                    Position position = new Position(x, y, floorLevel);
                    this.ps.put(position.hashCode(), position);
                }
            }
        }
    }

    @Override
    public int getFloorLevel() {
        return floorLevel;
    }

    @Override
    public List<Position> getTiles() {
        return new ArrayList<>(ps.values());
    }

    @Override
    public boolean contains(SceneNode entity) {
        return entity != null
               && ps.containsKey(entity.getPosition().hashCode());
    }
}
