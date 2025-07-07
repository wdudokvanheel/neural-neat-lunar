package nl.wdudokvanheel.neat.lunar.neural;


import nl.wdudokvanheel.neural.neat.CreatureFactory;
import nl.wdudokvanheel.neural.neat.genome.Genome;

public class LanderFactory implements CreatureFactory<NeatLander> {

	@Override
	public NeatLander createNewCreature(Genome genome){
		return new NeatLander(genome);
	}
}
