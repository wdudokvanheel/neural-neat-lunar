import com.bitechular.lunarlander.model.Level;
import com.bitechular.lunarlander.model.Line;
import com.bitechular.lunarlander.model.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class TestLevel extends Level{
	@Override
	protected List<Line> createLevel(int width, int height){
		ArrayList<Line> lines = new ArrayList<>();
		/* TEST LEVEL SETUP
		 * Start platform:	45-55, 100
		 * End platform:	245-255, 100
		 * Waypoint:		150, 50
		 */

		start = new Line(45, 100, 55, 100);
		target = new Line(245, 100, 255, 100);
		waypoint = new Vector2d(150, 50);
		return lines;
	}
}
