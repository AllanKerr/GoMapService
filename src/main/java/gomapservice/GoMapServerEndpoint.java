package gomapservice;

import static spark.Spark.port;
import static spark.Spark.post;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletResponse;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.IndexTreeList;
import org.mapdb.Serializer;

import com.github.aeonlucid.pogoprotos.Enums.TeamColor;
import com.github.aeonlucid.pogoprotos.GoMap.EncounterData;
import com.github.aeonlucid.pogoprotos.GoMap.GetPlayerInfoRequest;
import com.github.aeonlucid.pogoprotos.GoMap.GetPlayerInfoResponse;
import com.github.aeonlucid.pogoprotos.GoMap.GlobalActivityLog;
import com.github.aeonlucid.pogoprotos.GoMap.GymActivityType;
import com.github.aeonlucid.pogoprotos.GoMap.NameSearchRequest;
import com.github.aeonlucid.pogoprotos.GoMap.NameSearchResponse;
import com.github.aeonlucid.pogoprotos.GoMap.NotificationSetting;
import com.github.aeonlucid.pogoprotos.GoMap.NotificationType;
import com.github.aeonlucid.pogoprotos.GoMap.PlayerActivityLog;
import com.github.aeonlucid.pogoprotos.GoMap.RegisterTokenRequest;
import com.github.aeonlucid.pogoprotos.GoMap.UpdateDataRequest;
import com.github.aeonlucid.pogoprotos.GoMap.UpdateDataResponse;
import com.github.aeonlucid.pogoprotos.GoMap.UpdateNotificationsRequest;
import com.github.aeonlucid.pogoprotos.data.Gym.GymMembership;
import com.github.aeonlucid.pogoprotos.map.Fort.FortData;
import com.github.aeonlucid.pogoprotos.networking.Responses.GetGymDetailsResponse;

public class GoMapServerEndpoint {

	private static final int MAX_ACTIVITY_NUM = 50;

	private static final String CLIENT_SECRET = "2QDnZ7lbFZ7t6dkXAZoQLp4LBMRqg4Hf";

	private static final String SERVER_SECRET = "1GL3uH3x9E2u2w7T0bYR06gDcym0V6FF";

	private static final GoMapApnsEndpoint apnsEndpoint = new GoMapApnsEndpoint(new File("GoMapPush.p12"), SERVER_SECRET);
	
	private static final DB db = DBMaker.fileDB(new File("GoMapDB")).checksumHeaderBypass().closeOnJvmShutdown().make();

	@SuppressWarnings("unchecked")
	private static HTreeMap<String, GetGymDetailsResponse> getGyms() {
		return (HTreeMap<String, GetGymDetailsResponse>) db.hashMap("gyms").createOrOpen();
	}
	
	@SuppressWarnings("unchecked")
	private static HTreeMap<String, Player> getPlayers() {
		Serializer<Player> serializer = new PlayerSerializer();
		return (HTreeMap<String, Player>) db.hashMap("players").valueSerializer(serializer)
				.createOrOpen();
	}
	
	private static IndexTreeList<Object> getPokemon() {
		return db.indexTreeList("pokemon").createOrOpen();
	}
	
	private static IndexTreeList<Object> getGlobalActivity() {
		return db.indexTreeList("globalActivity").createOrOpen();
	}
	
	@SuppressWarnings("unchecked")
	private static HTreeMap<String, String> getTokens() {		
		return (HTreeMap<String, String>) db.hashMap("push_tokens").createOrOpen();
	}
	
	@SuppressWarnings("unchecked")
	private static HTreeMap<Integer, HashSet<String>> getNotificationSettings() {		
		return (HTreeMap<Integer, HashSet<String>>) db.hashMap("notification_settings").createOrOpen();
	}
	
	public static void main(String[] args) {
		port(9090);

		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    	System.out.println("Closing database...");
		    	db.close();
		    }
		 });
		
		post("/update_notifications", (req,res) -> {
			if (!req.headers("User-Agent").equals(CLIENT_SECRET)) {
				res.status(501);
        		return null;
			}
			UpdateNotificationsRequest request = UpdateNotificationsRequest.parseFrom(req.bodyAsBytes());
			HTreeMap<Integer, HashSet<String>> notificationSettings = getNotificationSettings();
			
			List<NotificationSetting> settings = request.getSettingsList();
			for (NotificationSetting setting : settings) {
				HashSet<String> deviceIdentifiers;
				Integer pokemonId = setting.getPokemonId().getNumber();
				boolean hasSetting = notificationSettings.containsKey(pokemonId);
				if ((setting.getNotificationType() == NotificationType.NONE)  && hasSetting) {
					deviceIdentifiers = notificationSettings.get(setting.getPokemonId().getNumber());
					deviceIdentifiers.remove(request.getDeviceIdentifier());
					if (deviceIdentifiers.size() == 0) {
						notificationSettings.remove(pokemonId);
					} else {
						notificationSettings.put(pokemonId, deviceIdentifiers);
					}
				} else if (setting.getNotificationType() == NotificationType.ALARM || 
						   setting.getNotificationType() == NotificationType.VIBRATE) {
					if (hasSetting) {
						deviceIdentifiers = notificationSettings.get(pokemonId);
					} else {
						deviceIdentifiers = new HashSet<String>();
					}
					deviceIdentifiers.add(request.getDeviceIdentifier());
					notificationSettings.put(pokemonId, deviceIdentifiers);
				}
			}
			db.commit();
			return "Success";
		});
		
		post("/register_token", (req,res) -> {
			if (!req.headers("User-Agent").equals(CLIENT_SECRET)) {
				res.status(501);
        		return null;
			}
			RegisterTokenRequest request = RegisterTokenRequest.parseFrom(req.bodyAsBytes());
			String token = request.getToken();
			String deviceIdentifier = request.getDeviceIdentifier();
			if (token != null && deviceIdentifier != null) {
				HTreeMap<String, String> tokens = getTokens();
				tokens.put(deviceIdentifier, token);
				db.commit();
			} else {
				res.status(1000);
			}
			return "Success";
		});
		
		post("/update_data", (req,res) -> {
			try {
				UpdateDataResponse.Builder builder = UpdateDataResponse.newBuilder();
				if (!req.headers("User-Agent").equals(CLIENT_SECRET)) {
					res.status(501);
	        		return builder.build();
				}
				UpdateDataRequest request = UpdateDataRequest.parseFrom(req.bodyAsBytes());

				IndexTreeList<Object> globalActivity = getGlobalActivity();
				ListIterator<Object> iterator = globalActivity.listIterator(globalActivity.size());
				while (iterator.hasPrevious()) {
					GlobalActivityLog activityLog = (GlobalActivityLog) iterator.previous();
					if (activityLog.getTimestamp() <= request.getTimestamp()) {
						break;
					}
					builder.addActivity(activityLog);
				}

				IndexTreeList<Object> pokemon = getPokemon();
				iterator = pokemon.listIterator(pokemon.size());
				while (iterator.hasPrevious()) {
					EncounterData encounterData = (EncounterData) iterator.previous();
					if (encounterData != null && encounterData.getFoundTime() > request.getTimestamp()) {
						builder.addEncounter(encounterData);
					}
				}

				HTreeMap<String, GetGymDetailsResponse> gyms = getGyms();
				@SuppressWarnings("unchecked")
				Iterator<GetGymDetailsResponse> gymIterator = gyms.values().iterator();
				while (gymIterator.hasNext()) {
					GetGymDetailsResponse gymDetails = gymIterator.next();
					if (gymDetails.getGymState().getFortData().getLastModifiedTimestampMs() > request.getTimestamp()) {
						builder.addGymsDetails(gymDetails);
					}
				}

				HttpServletResponse raw = res.raw();
				UpdateDataResponse response = builder.build();
				raw.getOutputStream().write(response.toByteArray());
				raw.getOutputStream().flush();
				raw.getOutputStream().close();
				return res.raw();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		
		post("/get_player", (req,res) -> {
			if (!req.headers("User-Agent").equals(CLIENT_SECRET)) {
        		res.status(501);
        		return null;
        	}
			HTreeMap<String, Player> players = getPlayers();
			GetPlayerInfoRequest request = GetPlayerInfoRequest.parseFrom(req.bodyAsBytes());
			Player player = players.get(request.getPlayerName());
			if (player == null) {
				return null;
			}
			HttpServletResponse raw = res.raw();
			GetPlayerInfoResponse response = player.build();
			raw.getOutputStream().write(response.toByteArray());
			raw.getOutputStream().flush();
			raw.getOutputStream().close();
			return res.raw();
		});
				
		post("/search_player", (req,res) -> {
			if (!req.headers("User-Agent").equals(CLIENT_SECRET)) {
        		res.status(501);
        		return null;
        	}
			HTreeMap<String, Player> players = getPlayers();
			NameSearchResponse.Builder builder = NameSearchResponse.newBuilder();
			NameSearchRequest request = NameSearchRequest.parseFrom(req.bodyAsBytes());
			String partial = request.getPlayerName().toLowerCase();
			if (partial!= null && partial.length() > 0) {
				@SuppressWarnings("unchecked")
				Iterator<String> names = players.keySet().iterator();
				while (names.hasNext()) {
					String name = names.next();
					if (name.toLowerCase().contains(partial)) {
						builder.addNames(name);
					}
				}	
			}
			HttpServletResponse raw = res.raw();
			NameSearchResponse response = builder.build();
			raw.getOutputStream().write(response.toByteArray());
			raw.getOutputStream().flush();
			raw.getOutputStream().close();
			return res.raw();
		});
		
		post("/inbound", (req, res) -> {
			if (!req.headers("User-Agent").equals(SERVER_SECRET)) {
				res.status(501);
        		return null;
			}
			UpdateDataResponse response = UpdateDataResponse.parseFrom(req.bodyAsBytes());
			System.out.println(response);
			
			long currentTime = System.currentTimeMillis();
			for (GetGymDetailsResponse gymDetails : response.getGymsDetailsList()) {
				GetGymDetailsResponse.Builder detailsBuilder = gymDetails.toBuilder();
				detailsBuilder.getGymStateBuilder().getFortDataBuilder().setLastModifiedTimestampMs(currentTime);
				receivedGymDetails(detailsBuilder.build());
			}
			
			IndexTreeList<Object> pokemon = getPokemon();
			HTreeMap<Integer, HashSet<String>> notificationSettings = getNotificationSettings();
			HTreeMap<String, String> tokenMap = null;
			for (EncounterData encounterData : response.getEncounterList()) {
				HashSet<String> deviceIdentifiers = notificationSettings.get(encounterData.getPokemonId().getNumber());
				if (deviceIdentifiers != null) {
					if (tokenMap == null) {
						tokenMap = getTokens();
					}
					ArrayList<String> tokens = new ArrayList<String>(deviceIdentifiers.size());
					for (String identifier : deviceIdentifiers) {
						String token = tokenMap.get(identifier);
						if (token != null) {
							tokens.add(token);
						}
					}
					apnsEndpoint.pushEncounter(encounterData, tokens);
				} 
				EncounterData.Builder encounterBuilder = encounterData.toBuilder();
				encounterBuilder.setFoundTime(currentTime);				
				pokemon.add(encounterBuilder.build());
			}
			ListIterator<Object> iterator = pokemon.listIterator();
			while (iterator.hasNext()) {
				EncounterData data = (EncounterData) iterator.next();
				if (data.getExpirationTime() <= currentTime) {
					iterator.remove();
				}				
			}
			IndexTreeList<Object> globalActivity = getGlobalActivity();
			while (globalActivity.size() > MAX_ACTIVITY_NUM) {
				globalActivity.remove(0);
			}
			db.commit();
			return "Success";
		});
	}
	
	private static void receivedGymDetails(GetGymDetailsResponse gymDetails) {
		HTreeMap<String, GetGymDetailsResponse> gyms = getGyms();
		
		String id = gymDetails.getGymState().getFortData().getId();
		GetGymDetailsResponse oldGym = gyms.get(id);
		if (oldGym != null) {
			if (gymDetails.getGymState().getFortData().getOwnedByTeam() != oldGym.getGymState().getFortData()
					.getOwnedByTeam()) {
				gymTaken(oldGym.getGymState().getMembershipsList(),
						oldGym.getGymState().getFortData().getOwnedByTeam(), gymDetails.getGymState().getMembershipsList(),
						gymDetails.getGymState().getFortData());
			} else {
				gymUpdated(oldGym.getGymState().getMembershipsList(), gymDetails.getGymState().getMembershipsList(),
						gymDetails.getGymState().getFortData());
			}
		} else {
			foundGym(gymDetails.getGymState().getMembershipsList(), gymDetails.getGymState().getFortData());
		}
		gyms.put(gymDetails.getGymState().getFortData().getId(), gymDetails);
	}
	
	private static void gymTaken(List<GymMembership> oldMembers, TeamColor oldTeam, List<GymMembership> newMembers,
			FortData fortData) {

		HTreeMap<String, Player> players = getPlayers();
		IndexTreeList<Object> globalActivity = getGlobalActivity();

		for (GymMembership member : oldMembers) {
			Player player;
			String name = member.getTrainerPublicProfile().getName();
			if (!players.containsKey(name)) {
				player = new Player(fortData.getOwnedByTeam());
			} else {
				player = players.get(name);
				player.removeGym(fortData.getId());
			}
			player.addPokemon(member.getPokemonData());
			players.put(name, player);
		}
		PlayerActivityLog.Builder logBuilder = PlayerActivityLog.newBuilder();
		logBuilder.setTimestamp(fortData.getLastModifiedTimestampMs());
		logBuilder.setGymId(fortData.getId());
		logBuilder.setType(GymActivityType.ACTIVITY_TAKEDOWN);
		
		PlayerActivityLog activityLog = logBuilder.build();
		for (GymMembership member : newMembers) {
			Player player;
			String name = member.getTrainerPublicProfile().getName();
			if (!players.containsKey(name)) {
				player = new Player(fortData.getOwnedByTeam());
			} else {
				player = players.get(name);
			}
			GlobalActivityLog.Builder globalLogBuilder = GlobalActivityLog.newBuilder();
			globalLogBuilder.setTimestamp(System.currentTimeMillis());
			globalLogBuilder.setTeam(fortData.getOwnedByTeam());
			globalLogBuilder.setGymId(fortData.getId());
			globalLogBuilder.setPlayerName(name);
			globalLogBuilder.setType(GymActivityType.ACTIVITY_TAKEDOWN);
			globalActivity.add(globalLogBuilder.build());
			
			player.addGym(fortData.getId());
			player.addActivityLog(activityLog);
			player.addPokemon(member.getPokemonData());
			players.put(name, player);
		}
	}
	
	private static void gymUpdated(List<GymMembership> oldMembers, List<GymMembership> newMembers, FortData fortData) {

		HTreeMap<String, Player> players = getPlayers();
		IndexTreeList<Object> globalActivity = getGlobalActivity();
		
		for (GymMembership member : oldMembers) {
			boolean recentlyRemoved = true;
			String name = member.getTrainerPublicProfile().getName();
			ListIterator<GymMembership> newMembersIterator = newMembers.listIterator();
			while (recentlyRemoved && newMembersIterator.hasNext()) {
				GymMembership newMember = newMembersIterator.next();
				recentlyRemoved = !name.equals(newMember.getTrainerPublicProfile().getName());
			}
			if (recentlyRemoved) {
				Player player;
				if (!players.containsKey(name)) {
					player = new Player(fortData.getOwnedByTeam());
				} else {
					player = players.get(name);
				}
				player.removeGym(fortData.getId());
				players.put(name, player);
			}
		}
		PlayerActivityLog.Builder logBuilder = PlayerActivityLog.newBuilder();
		logBuilder.setTimestamp(fortData.getLastModifiedTimestampMs());
		logBuilder.setGymId(fortData.getId());
		logBuilder.setType(GymActivityType.ACTIVITY_ADDED);
		
		PlayerActivityLog activityLog = logBuilder.build();
		for (GymMembership member : newMembers) {
			Player player;
			String name = member.getTrainerPublicProfile().getName();

			if (!players.containsKey(name)) {
				player = new Player(fortData.getOwnedByTeam());
			} else {
				player = players.get(name);
			}
			boolean recentlyAdded = true;
			ListIterator<GymMembership> oldMembersIterator = oldMembers.listIterator();
			while (recentlyAdded && oldMembersIterator.hasNext()) {
				GymMembership oldMember = oldMembersIterator.next();
				recentlyAdded = !name.equals(oldMember.getTrainerPublicProfile().getName());
			}
			if (recentlyAdded) {
				GlobalActivityLog.Builder globalLogBuilder = GlobalActivityLog.newBuilder();
				globalLogBuilder.setTimestamp(System.currentTimeMillis());
				globalLogBuilder.setTeam(fortData.getOwnedByTeam());
				globalLogBuilder.setGymId(fortData.getId());
				globalLogBuilder.setPlayerName(name);
				globalLogBuilder.setType(GymActivityType.ACTIVITY_ADDED);
				globalActivity.add(globalLogBuilder.build());
				
				player.addActivityLog(activityLog);
			}
			player.addGym(fortData.getId());
			player.addPokemon(member.getPokemonData());
			players.put(name, player);
		}
	}
	
	private static void foundGym(List<GymMembership> members, FortData fortData) {
		HTreeMap<String, Player> players = getPlayers();		
		for (GymMembership member : members) {
			Player player;
			String name = member.getTrainerPublicProfile().getName();

			if (!players.containsKey(name)) {
				player = new Player(fortData.getOwnedByTeam());
			} else {
				player = players.get(name);
			}
			player.addGym(fortData.getId());
			player.addPokemon(member.getPokemonData());
			players.put(name, player);
		}
	}
}
