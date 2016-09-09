package gomapservice;

import com.github.aeonlucid.pogoprotos.Enums.PokemonId;

public class Util {
	public static boolean requiresNotification(PokemonId pokemonId) {
		switch (pokemonId) {
		case MISSINGNO:
		case BULBASAUR:
		case IVYSAUR:
		case CHARMANDER:
		case CHARMELEON:
		case SQUIRTLE:
		case CATERPIE:
		case METAPOD:
		case BUTTERFREE:
		case WEEDLE:
		case KAKUNA:
		case BEEDRILL:
		case PIDGEY:
		case PIDGEOTTO:
		case PIDGEOT:
		case RATTATA:
		case RATICATE:
		case SPEAROW:
		case FEAROW:
		case EKANS:
		case ARBOK:
		case PIKACHU:
		case RAICHU:
		case SANDSHREW:
		case SANDSLASH:
		case NIDORAN_FEMALE:
		case NIDORINA:
		case NIDOQUEEN:
		case NIDORAN_MALE:
		case NIDORINO:
		case NIDOKING:
		case CLEFAIRY:
		case CLEFABLE:
		case VULPIX:
		case NINETALES:
		case JIGGLYPUFF:
		case WIGGLYTUFF:
		case ZUBAT:
		case GOLBAT:
		case ODDISH:
		case GLOOM:
		case PARAS:
		case PARASECT:
		case VENONAT:
		case VENOMOTH:
		case DIGLETT:
		case DUGTRIO:
		case MEOWTH:
		case PERSIAN:
		case PSYDUCK:
		case MANKEY:
		case PRIMEAPE:
		case GROWLITHE:
		case ARCANINE:
		case POLIWAG:
		case POLIWHIRL:
		case ABRA:
		case KADABRA:
		case ALAKAZAM:
		case MACHOP:
		case MACHOKE:
		case MACHAMP:
		case BELLSPROUT:
		case WEEPINBELL:
		case TENTACOOL:
		case GEODUDE:
		case GRAVELER:
		case GOLEM:
		case PONYTA:
		case RAPIDASH:
		case SLOWPOKE:
		case MAGNEMITE:
		case MAGNETON:
		case DODUO:
		case DODRIO:
		case SEEL:
		case SHELLDER:
		case GASTLY:
		case HAUNTER:
		case ONIX:
		case DROWZEE:
		case HYPNO:
		case KRABBY:
		case KINGLER:
		case VOLTORB:
		case ELECTRODE:
		case EXEGGCUTE:
		case CUBONE:
		case MAROWAK:
		case HITMONLEE:
		case HITMONCHAN:
		case LICKITUNG:
		case KOFFING:
		case WEEZING:
		case RHYHORN:
		case RHYDON:
		case CHANSEY:
		case TANGELA:
		case HORSEA:
		case GOLDEEN:
		case STARYU:
		case SCYTHER:
		case JYNX:
		case ELECTABUZZ:
		case MAGMAR:
		case PINSIR:
		case MAGIKARP:
		case EEVEE:
		case JOLTEON:
		case FLAREON:
		case PORYGON:
		case OMANYTE:
		case OMASTAR:
		case KABUTO:
		case KABUTOPS:
		case AERODACTYL:
			return false;
		default:
			return true;
		}
	}
	
	public static String soundFileForPokemonId(PokemonId pokemonId) {
		switch (pokemonId) {
		case BLASTOISE:
		case POLIWRATH:
		case SLOWBRO:
		case FARFETCHD:
		case MUK:
		case KANGASKHAN:
		case MR_MIME:
		case TAUROS:
		case GYARADOS:
		case LAPRAS:
		case DITTO:
		case ARTICUNO:
		case ZAPDOS:
		case MOLTRES:
		case DRAGONITE:
			return "alarm.caf";
		default:
			return "default";
		}
	}
	
	public static String nameForPokemonId(PokemonId pokemonId) {
		switch (pokemonId) {
		case MISSINGNO:
			return "Missingno";
		case BULBASAUR:
			return "Bulbasaur";
		case IVYSAUR:
			return "Ivysaur";
		case VENUSAUR:
			return "Venusaur";
		case CHARMANDER:
			return "Charmander";
		case CHARMELEON:
			return "Charmeleon";
		case CHARIZARD:
			return "Charizard";
		case SQUIRTLE:
			return "Squirtle";
		case WARTORTLE:
			return "Wartortle";
		case BLASTOISE:
			return "Blastoise";
		case CATERPIE:
			return "Caterpie";
		case METAPOD:
			return "Metapod";
		case BUTTERFREE:
			return "Butterfree";
		case WEEDLE:
			return "Weedle";
		case KAKUNA:
			return "Kakuna";
		case BEEDRILL:
			return "Beedrill";
		case PIDGEY:
			return "Pidgey";
		case PIDGEOTTO:
			return "Pidgeotto";
		case PIDGEOT:
			return "Pidgeot";
		case RATTATA:
			return "Rattata";
		case RATICATE:
			return "Raticate";
		case SPEAROW:
			return "Spearow";
		case FEAROW:
			return "Fearow";
		case EKANS:
			return "Ekans";
		case ARBOK:
			return "Arbok";
		case PIKACHU:
			return "Pikachu";
		case RAICHU:
			return "Raichu";
		case SANDSHREW:
			return "Sandshrew";
		case SANDSLASH:
			return "Sandslash";
		case NIDORAN_FEMALE:
			return "Nidoran Female";
		case NIDORINA:
			return "Nidorina";
		case NIDOQUEEN:
			return "Nidoqueen";
		case NIDORAN_MALE:
			return "Nidoran Male";
		case NIDORINO:
			return "Nidorino";
		case NIDOKING:
			return "Nidoking";
		case CLEFAIRY:
			return "Clefairy";
		case CLEFABLE:
			return "Clefable";
		case VULPIX:
			return "Vulpix";
		case NINETALES:
			return "Ninetales";
		case JIGGLYPUFF:
			return "Jigglypuff";
		case WIGGLYTUFF:
			return "Wigglytuff";
		case ZUBAT:
			return "Zubat";
		case GOLBAT:
			return "Golbat";
		case ODDISH:
			return "Oddish";
		case GLOOM:
			return "Gloom";
		case VILEPLUME:
			return "Vileplume";
		case PARAS:
			return "Paras";
		case PARASECT:
			return "Parasect";
		case VENONAT:
			return "Venonat";
		case VENOMOTH:
			return "Venomoth";
		case DIGLETT:
			return "Diglett";
		case DUGTRIO:
			return "Dugtrio";
		case MEOWTH:
			return "Meowth";
		case PERSIAN:
			return "Persian";
		case PSYDUCK:
			return "Psyduck";
		case GOLDUCK:
			return "Golduck";
		case MANKEY:
			return "Mankey";
		case PRIMEAPE:
			return "Primeape";
		case GROWLITHE:
			return "Growlithe";
		case ARCANINE:
			return "Arcanine";
		case POLIWAG:
			return "Poliwag";
		case POLIWHIRL:
			return "Poliwhirl";
		case POLIWRATH:
			return "Poliwrath";
		case ABRA:
			return "Abra";
		case KADABRA:
			return "Kadabra";
		case ALAKAZAM:
			return "Alakazam";
		case MACHOP:
			return "Machop";
		case MACHOKE:
			return "Machoke";
		case MACHAMP:
			return "Machamp";
		case BELLSPROUT:
			return "Bellsprout";
		case WEEPINBELL:
			return "Weepinbell";
		case VICTREEBEL:
			return "Victreebel";
		case TENTACOOL:
			return "Tentacool";
		case TENTACRUEL:
			return "Tentacruel";
		case GEODUDE:
			return "Geodude";
		case GRAVELER:
			return "Graveler";
		case GOLEM:
			return "Golem";
		case PONYTA:
			return "Ponyta";
		case RAPIDASH:
			return "Rapidash";
		case SLOWPOKE:
			return "Slowpoke";
		case SLOWBRO:
			return "Slowbro";
		case MAGNEMITE:
			return "Magnemite";
		case MAGNETON:
			return "Magneton";
		case FARFETCHD:
			return "Farfetchd";
		case DODUO:
			return "Doduo";
		case DODRIO:
			return "Dodrio";
		case SEEL:
			return "Seel";
		case DEWGONG:
			return "Dewgong";
		case GRIMER:
			return "Grimer";
		case MUK:
			return "Muk";
		case SHELLDER:
			return "Shellder";
		case CLOYSTER:
			return "Cloyster";
		case GASTLY:
			return "Gastly";
		case HAUNTER:
			return "Haunter";
		case GENGAR:
			return "Gengar";
		case ONIX:
			return "Onix";
		case DROWZEE:
			return "Drowzee";
		case HYPNO:
			return "Hypno";
		case KRABBY:
			return "Krabby";
		case KINGLER:
			return "Kingler";
		case VOLTORB:
			return "Voltorb";
		case ELECTRODE:
			return "Electrode";
		case EXEGGCUTE:
			return "Exeggcute";
		case EXEGGUTOR:
			return "Exeggutor";
		case CUBONE:
			return "Cubone";
		case MAROWAK:
			return "Marowak";
		case HITMONLEE:
			return "Hitmonlee";
		case HITMONCHAN:
			return "Hitmonchan";
		case LICKITUNG:
			return "Lickitung";
		case KOFFING:
			return "Koffing";
		case WEEZING:
			return "Weezing";
		case RHYHORN:
			return "Rhyhorn";
		case RHYDON:
			return "Rhydon";
		case CHANSEY:
			return "Chansey";
		case TANGELA:
			return "Tangela";
		case KANGASKHAN:
			return "Kangaskhan";
		case HORSEA:
			return "Horsea";
		case SEADRA:
			return "Seadra";
		case GOLDEEN:
			return "Goldeen";
		case SEAKING:
			return "Seaking";
		case STARYU:
			return "Staryu";
		case STARMIE:
			return "Starmie";
		case MR_MIME:
			return "MrMime";
		case SCYTHER:
			return "Scyther";
		case JYNX:
			return "Jynx";
		case ELECTABUZZ:
			return "Electabuzz";
		case MAGMAR:
			return "Magmar";
		case PINSIR:
			return "Pinsir";
		case TAUROS:
			return "Tauros";
		case MAGIKARP:
			return "Magikarp";
		case GYARADOS:
			return "Gyarados";
		case LAPRAS:
			return "Lapras";
		case DITTO:
			return "Ditto";
		case EEVEE:
			return "Eevee";
		case VAPOREON:
			return "Vaporeon";
		case JOLTEON:
			return "Jolteon";
		case FLAREON:
			return "Flareon";
		case PORYGON:
			return "Porygon";
		case OMANYTE:
			return "Omanyte";
		case OMASTAR:
			return "Omastar";
		case KABUTO:
			return "Kabuto";
		case KABUTOPS:
			return "Kabutops";
		case AERODACTYL:
			return "Aerodactyl";
		case SNORLAX:
			return "Snorlax";
		case ARTICUNO:
			return "Articuno";
		case ZAPDOS:
			return "Zapdos";
		case MOLTRES:
			return "Moltres";
		case DRATINI:
			return "Dratini";
		case DRAGONAIR:
			return "Dragonair";
		case DRAGONITE:
			return "Dragonite";
		case MEWTWO:
			return "Mewtwo";
		case MEW:
			return "Mew";
		default:
			return "Unknown";
		}
	}
}
