package gomapservice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.github.aeonlucid.pogoprotos.Data.PokemonData;
import com.github.aeonlucid.pogoprotos.Enums.TeamColor;
import com.github.aeonlucid.pogoprotos.GoMap.GetPlayerInfoResponse;
import com.github.aeonlucid.pogoprotos.GoMap.GymHistory;
import com.github.aeonlucid.pogoprotos.GoMap.PlayerActivityLog;

public class Player {
	
	private TeamColor team;

	private HashMap<Long, PokemonData> pokemons = new HashMap<Long, PokemonData>();

	private HashMap<String, GymHistory> gym_history = new HashMap<String, GymHistory>();

	private LinkedList<PlayerActivityLog> activity_logs = new LinkedList<PlayerActivityLog>();
	
	private HashSet<String> gym_ids = new HashSet<String>();

	public Player(TeamColor team) {
		this.team = team;
	}
	
	public void addGym(String gymID) {
		if (!gym_ids.contains(gymID)) {
			GymHistory.Builder historyBuilder = GymHistory.newBuilder();
			GymHistory oldHistory = gym_history.get(gymID);
			if (oldHistory != null) {
				historyBuilder.setNum(oldHistory.getNum() + 1);
				historyBuilder.setGymId(gymID);
			} else {
				historyBuilder.setNum(1);
				historyBuilder.setGymId(gymID);
			}
			this.gym_history.put(gymID, historyBuilder.build());
			this.gym_ids.add(gymID);	
		}
	}
	
	public void removeGym(String gymID) {
		this.gym_ids.remove(gymID);
	}
	
	public void addActivityLog(PlayerActivityLog activityLog) {
		activity_logs.add(activityLog);
	}
	
	public void addPokemon(PokemonData pokemon) {
		pokemons.put(pokemon.getId(), pokemon);
	}
	
	public GetPlayerInfoResponse build() {
		GetPlayerInfoResponse.Builder builder = GetPlayerInfoResponse.newBuilder();
		builder.setTeamColor(team);
		for (PokemonData data : this.pokemons.values()) {
			builder.addPokemon(data);
		}
		for (GymHistory history : this.gym_history.values()) {
			builder.addGymHistory(history);
		}
		long yesterday = System.currentTimeMillis() - 86400000;
		for (PlayerActivityLog log : this.activity_logs) {
			if (log.getTimestamp() >= yesterday) {
				builder.addRecentActivity(log);
			}
		}
		for (String gymID : this.gym_ids) {
			builder.addGymIds(gymID);
		}
		return builder.build();
	}
	
	public Player(GetPlayerInfoResponse response) {
		this.team = response.getTeamColor();
		for (PokemonData data : response.getPokemonList()) {
			this.addPokemon(data);
		}
		for (PlayerActivityLog log : response.getRecentActivityList()) {
			this.addActivityLog(log);
		}
		for (String gymID : response.getGymIdsList()) {
			this.gym_ids.add(gymID);
		}		
		for (GymHistory history : response.getGymHistoryList()) {
			this.gym_history.put(history.getGymId(), history);
		}
	}
}
