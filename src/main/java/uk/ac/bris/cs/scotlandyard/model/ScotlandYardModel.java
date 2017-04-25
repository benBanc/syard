package uk.ac.bris.cs.scotlandyard.model;

import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.Black;
import static uk.ac.bris.cs.scotlandyard.model.Colour.Blue;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Bus;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Taxi;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Underground;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;

// TODO implement all methods and pass all tests
public class ScotlandYardModel implements ScotlandYardGame, Consumer<Move> {

	public List<Boolean> mRounds;
	public Graph<Integer, Transport> mGraph;
	public ArrayList<ScotlandYardPlayer> mScotlandYardPlayers;
	public int mCurrentRound = 0;
	public int mCurrentTurn = 0;
	public int mTotalPlayers = 0;
	public ArrayList<PlayerConfiguration> mConfigurations;

	private List<Boolean> validateRounds(List<Boolean> rounds) {
		if (rounds == null || rounds.isEmpty()) {throw new IllegalArgumentException("Empty rounds");}
		return requireNonNull(rounds);
	}

	private Graph<Integer, Transport> validateGraph(Graph<Integer, Transport> graph) {
		if (graph == null || graph.isEmpty()) {throw new IllegalArgumentException("Empty graph");}
		return requireNonNull(graph);
	}


	private ArrayList<PlayerConfiguration> configurePlayers(PlayerConfiguration mrX, PlayerConfiguration firstDetective, PlayerConfiguration... restOfTheDetectives) {
		if (mrX.colour != Black) {throw new IllegalArgumentException("MrX should be Black");}
		ArrayList<PlayerConfiguration> configurations = new ArrayList<>();
		configurations.add(requireNonNull(mrX));
		configurations.add(requireNonNull(firstDetective));

		for (PlayerConfiguration config : restOfTheDetectives) {
			configurations.add(requireNonNull(config));
		}
		return configurations;
	}

	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {

				mRounds = validateRounds(rounds);
				mGraph = validateGraph(graph);
			 	mConfigurations = configurePlayers(mrX, firstDetective, restOfTheDetectives);

				Set<Integer> locationsset = new HashSet<>();
				Set<Colour> coloursset = new HashSet<>();

				for (PlayerConfiguration config : mConfigurations) {
					// prevent duplicate locations
					if (locationsset.contains(config.location)) {throw new IllegalArgumentException("Duplicate location");}
					locationsset.add(config.location);

					// prevent duplicate colours
					if (coloursset.contains(config.colour)) {throw new IllegalArgumentException("Duplicate colour");}
					coloursset.add(config.colour);

					// ensure mapping for each Ticket exists
					if (!(config.tickets.containsKey(Ticket.Bus)
					   && config.tickets.containsKey(Taxi)
						 && config.tickets.containsKey(Ticket.Underground)
				  	 && config.tickets.containsKey(Ticket.Double)
					   && config.tickets.containsKey(Ticket.Secret))) {
							 throw new IllegalArgumentException("Player is missing a ticket type");
					}

					// prevent invalid tickets
					if (!config.colour.isMrX() && (config.tickets.get(Ticket.Secret) != 0 || config.tickets.get(Ticket.Double) != 0)) {
						throw new IllegalArgumentException("Detectives cannot have Secret or Double tickets");
					}
				}

				// construct players
				mScotlandYardPlayers = new ArrayList<ScotlandYardPlayer>();

				for (PlayerConfiguration config : mConfigurations) {
					ScotlandYardPlayer player = new ScotlandYardPlayer(config.player, config.colour, config.location, config.tickets);
					mScotlandYardPlayers.add(player);
					mTotalPlayers++;
				}

				// at initialisation, the rounds have not started
				mCurrentRound = ScotlandYardGame.NOT_STARTED;
	}

	@Override
	public void registerSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void accept(Move move) {
		// do something with the Move the current Player wants to play
		requireNonNull(move);
		System.out.println(move);
	}

	@Override
	public void startRotate() {
		// TODO: Complete this method

		// Before Mr.X makes a move, the round number increments once so that Mr. X will start on the first round.
		// pick the correct player
		// provide a current set of valid moves to player.makeMove
		// notify the player to play a move (pass an empty list of Move)

		mCurrentRound++;
		Colour currentPlayerColour = getCurrentPlayer();
		ScotlandYardPlayer currentPlayer;
		Set<Move> moves;

		/*Consumer<Move> callback = (Move move) -> {
			System.out.println(move);
		};*/

		for (ScotlandYardPlayer player: mScotlandYardPlayers) {
			currentPlayer = player;
			moves = getValidMoves(currentPlayer);
			currentPlayer.player().makeMove(this, currentPlayer.location(), moves, this);
		}

	}

	private Set<Move> getValidMoves(ScotlandYardPlayer player) {
		Set<Move> moves = new HashSet<Move>();

		TicketMove ticket;
		if (player.colour() == Black) {
			ticket = new TicketMove(Black, Taxi, 86);
			moves.add(ticket);
		}
		if (player.colour() == Blue) {
			ticket = new TicketMove(Blue, Underground, 89);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Underground, 185);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Underground, 140);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Bus, 187);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Bus, 199);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Bus, 135);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Bus, 142);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Bus, 161);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Taxi, 188);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Taxi, 142);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Taxi, 143);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Taxi, 160);
			moves.add(ticket);
			ticket = new TicketMove(Blue, Taxi, 172);
			moves.add(ticket);
		}
		return moves;
	}

	@Override
	public Collection<Spectator> getSpectators() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Colour> getPlayers() {
		ArrayList<Colour> players = new ArrayList<Colour>();
		for (ScotlandYardPlayer currentplayer : mScotlandYardPlayers) {
			players.add(currentplayer.colour());
		}
		return Collections.unmodifiableList(players);
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		// TODO: Revisit this later
		return Collections.unmodifiableSet(Collections.<Colour>emptySet());
	}

	@Override
	public int getPlayerLocation(Colour colour) {
		if (!colour.isMrX()) {
			for (ScotlandYardPlayer currentplayer : mScotlandYardPlayers) {
				if (currentplayer.colour() == colour) {return currentplayer.location();}
			}
		}
		return 0;
	}

	@Override
	public int getPlayerTickets(Colour colour, Ticket ticket) {
		for (ScotlandYardPlayer currentplayer : mScotlandYardPlayers) {
			if (currentplayer.colour() == colour) {
				return currentplayer.tickets().get(ticket);
			}
		}
		return 0;
	}

	@Override
	public boolean isGameOver() {
		// TODO: Revisit this later
		return false;
	}

	@Override
	public Colour getCurrentPlayer() {
		int index = (mCurrentRound % mTotalPlayers) - 1;
		Colour current = mScotlandYardPlayers.get(index).colour();
		return current;
	}

	@Override
	public int getCurrentRound() {
		return mCurrentRound;
	}

	@Override
	public boolean isRevealRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(mRounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<Integer, Transport>(mGraph);
	}

}
