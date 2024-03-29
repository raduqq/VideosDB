package fileio;

import actor.Actor;
import common.Constants;
import database.ActorsDB;
import database.GenreDB;
import database.UsersDB;
import database.VideosDB;
import entertainment.Movie;
import entertainment.Season;
import entertainment.Show;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import user.User;
import utils.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class reads and parses the data from the tests
 * <p>
 * DO NOT MODIFY
 */
public final class InputLoader {
    /**
     * The path to the input file
     */
    private final String inputPath;

    public InputLoader(final String inputPath) {
        this.inputPath = inputPath;
    }

    public String getInputPath() {
        return inputPath;
    }

    /**
     * The method reads the database
     * @return an Input object
     */
    public Input readData() {
        JSONParser jsonParser = new JSONParser();
        List<ActionInputData> actions = null;
        List<ActorInputData> actors = new ArrayList<>();
        List<UserInputData> users = new ArrayList<>();
        List<MovieInputData> movies = new ArrayList<>();
        List<SerialInputData> serials = new ArrayList<>();

        // My custom databases
        UsersDB myUserDB = new UsersDB();
        ActorsDB myActorDB = new ActorsDB();
        GenreDB myGenreDB = new GenreDB();
        VideosDB myVideoDB = new VideosDB();

        try {
            // Parsing the contents of the JSON file
            JSONObject jsonObject = (JSONObject) jsonParser
                    .parse(new FileReader(inputPath));
            JSONObject database = (JSONObject) jsonObject.get(Constants.DATABASE);
            JSONArray jsonActors = (JSONArray)
                    database.get(Constants.ACTORS);
            JSONArray jsonUsers = (JSONArray)
                    database.get(Constants.USERS);
            JSONArray jsonMovies = (JSONArray)
                    database.get(Constants.MOVIES);
            JSONArray jsonSerial = (JSONArray)
                    database.get(Constants.SHOWS);

            if (jsonActors != null) {
                for (Object jsonActor : jsonActors) {
                    actors.add(new ActorInputData(
                            (String) ((JSONObject) jsonActor).get(Constants.NAME),
                            (String) ((JSONObject) jsonActor).get(Constants.DESCRIPTION),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonActor)
                                    .get(Constants.FILMOGRAPHY)),
                            Utils.convertAwards((JSONArray) ((JSONObject) jsonActor)
                                    .get(Constants.AWARDS))
                    ));

                    // Adding my custom actor to my custom actor DB
                    myActorDB.addToDB(new Actor(
                            (String) ((JSONObject) jsonActor).get(Constants.NAME),
                            (String) ((JSONObject) jsonActor).get(Constants.DESCRIPTION),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonActor)
                                    .get(Constants.FILMOGRAPHY)),
                            Utils.convertAwards((JSONArray) ((JSONObject) jsonActor)
                                    .get(Constants.AWARDS))
                    ));
                }
            } else {
                System.out.println("NU EXISTA ACTORI");
            }

            if (jsonUsers != null) {
                for (Object jsonUser : jsonUsers) {
                    users.add(new UserInputData(
                            (String) ((JSONObject) jsonUser).get(Constants.USERNAME),
                            (String) ((JSONObject) jsonUser).get(Constants.SUBSCRIPTION),
                            Utils.watchedMovie((JSONArray) ((JSONObject) jsonUser)
                                    .get(Constants.HISTORY)),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonUser)
                                    .get(Constants.FAVORITE_MOVIES))
                    ));

                    // Adding my custom user to my custom user DB
                    myUserDB.addToDB(new User(
                            (String) ((JSONObject) jsonUser).get(Constants.USERNAME),
                            (String) ((JSONObject) jsonUser).get(Constants.SUBSCRIPTION),
                            Utils.watchedMovie((JSONArray) ((JSONObject) jsonUser)
                                    .get(Constants.HISTORY)),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonUser)
                                    .get(Constants.FAVORITE_MOVIES))
                    ));
                }
            } else {
                System.out.println("NU EXISTA UTILIZATORI");
            }

            if (jsonMovies != null) {
                for (Object jsonIterator : jsonMovies) {
                    movies.add(new MovieInputData(
                            (String) ((JSONObject) jsonIterator).get(Constants.NAME),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.ACTORS)),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.GENRES)),
                            Integer.parseInt(((JSONObject) jsonIterator).get(Constants.YEAR)
                                    .toString()),
                            Integer.parseInt(((JSONObject) jsonIterator).get(Constants.DURATION)
                                    .toString())
                    ));

                    // Adding movie to my custom video database
                    myVideoDB.addToDB(new Movie(
                            (String) ((JSONObject) jsonIterator).get(Constants.NAME),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.ACTORS)),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.GENRES)),
                            Integer.parseInt(((JSONObject) jsonIterator).get(Constants.YEAR)
                                    .toString()),
                            Integer.parseInt(((JSONObject) jsonIterator).get(Constants.DURATION)
                                    .toString())
                    ));

                    // Increasing movie count in database
                    myVideoDB.incNoMovies();

                    // Mapping movie to its genre
                    String currMovieName = (String) ((JSONObject) jsonIterator).get(Constants.NAME);
                    List<String> currGenres = Utils
                            .convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                            .get(Constants.GENRES));

                    for (String currGenre : currGenres) {
                        myGenreDB.addToMap(currGenre, currMovieName);
                    }
                }
            } else {
                System.out.println("NU EXISTA FILME");
            }

            if (jsonSerial != null) {
                for (Object jsonIterator : jsonSerial) {

                    ArrayList<Season> seasons = new ArrayList<>();

                    if (((JSONObject) jsonIterator).get(Constants.SEASONS) != null) {
                        for (Object iterator : (JSONArray) ((JSONObject) jsonIterator)
                                .get(Constants.SEASONS)) {
                            seasons.add(new Season(
                                    ((Long) ((JSONObject) iterator).get(Constants.CURRENT_SEASON))
                                            .intValue(),
                                    ((Long) ((JSONObject) iterator).get(Constants.DURATION))
                                            .intValue()
                            ));
                        }
                    } else {
                        seasons = null;
                    }

                    serials.add(new SerialInputData(
                            (String) ((JSONObject) jsonIterator).get(Constants.NAME),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.CAST)),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.GENRES)),
                            ((Long) ((JSONObject) jsonIterator).get(Constants.NUMBER_OF_SEASONS))
                                    .intValue(),
                            seasons,
                            Integer.parseInt(((JSONObject) jsonIterator).get(Constants.YEAR)
                                    .toString())
                    ));

                    // Adding show to my custom video database
                    myVideoDB.addToDB(new Show((String) ((JSONObject) jsonIterator)
                            .get(Constants.NAME),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.CAST)),
                            Utils.convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                                    .get(Constants.GENRES)),
                            ((Long) ((JSONObject) jsonIterator).get(Constants.NUMBER_OF_SEASONS))
                                    .intValue(),
                            seasons,
                            Integer.parseInt(((JSONObject) jsonIterator).get(Constants.YEAR)
                                    .toString())
                    ));

                    // Increasing show count in database
                    myVideoDB.incNoShows();

                    // Mapping show to its genre
                    String currShowName = (String) ((JSONObject) jsonIterator).get(Constants.NAME);
                    List<String> currGenres = Utils
                            .convertJSONArray((JSONArray) ((JSONObject) jsonIterator)
                            .get(Constants.GENRES));

                    for (String currGenre : currGenres) {
                        myGenreDB.addToMap(currGenre, currShowName);
                    }
                }
            } else {
                System.out.println("NU EXISTA SERIALE");
            }

            /*
              Now that users, movies and shows were added to
              their respective databases, we can compute
              the initial favCount + viewCount of all videos
             */
            myVideoDB.computeInitialFavCount(myUserDB);
            myVideoDB.computeInitialViewCount(myUserDB);

            actions = readActions(jsonObject, Math.max(Math.max(movies.size()
                    + serials.size(), users.size()), actors.size()));

            if (jsonActors == null) {
                actors = null;
            }

            if (jsonMovies == null) {
                movies = null;
            }

            if (jsonSerial == null) {
                serials = null;
            }

            if (jsonUsers == null) {
                users = null;
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return new Input(actors, users, actions, movies, serials,
                        myUserDB, myActorDB, myGenreDB, myVideoDB);
    }

    /**
     * The method reads the actions from input file
     * @param jsonObject jsonObject
     * @param size size
     * @return A list of actions
     */
    public List<ActionInputData> readActions(final JSONObject jsonObject, final int size) {

        List<ActionInputData> actions = new ArrayList<>();
        JSONArray jsonActions = (JSONArray)
                jsonObject.get(Constants.ACTIONS);

        if (jsonActions != null) {
            for (Object jsonIterator : jsonActions) {
                String actionType = (String) ((JSONObject) jsonIterator)
                        .get(Constants.ACTION_TYPE);
                double grade = 0;
                int season = 0;

                if (((JSONObject) jsonIterator).get(Constants.SEASON) != null) {
                    season = Integer.parseInt(((JSONObject) jsonIterator)
                            .get(Constants.SEASON).toString());
                }

                if (((JSONObject) jsonIterator).get(Constants.GRADE) != null) {
                    grade = Double.parseDouble(((JSONObject) jsonIterator).get(Constants.GRADE)
                            .toString());
                }

                String genre = null;
                String year = null;
                JSONArray awards = null;
                JSONArray words = null;

                int number;

                if (((JSONObject) jsonIterator).get(Constants.NUMBER) != null) {
                    number = Integer.parseInt(((JSONObject) jsonIterator)
                            .get(Constants.NUMBER).toString());
                } else {
                    number = size;
                }

                if (((JSONObject) jsonIterator).get(Constants.FILTERS) != null) {
                    genre = (String) ((JSONObject) ((JSONObject) jsonIterator)
                            .get(Constants.FILTERS))
                            .get(Constants.GENRE);

                    year = (String) ((JSONObject) ((JSONObject) jsonIterator)
                            .get(Constants.FILTERS))
                            .get(Constants.YEAR);

                    awards = (JSONArray) ((JSONObject) ((JSONObject) jsonIterator)
                            .get(Constants.FILTERS))
                            .get(Constants.AWARDS);

                    words = (JSONArray) ((JSONObject) ((JSONObject) jsonIterator)
                            .get(Constants.FILTERS))
                            .get(Constants.WORDS);
                }

                switch (actionType) {

                        case Constants.COMMAND -> actions.add(new ActionInputData(
                                Integer.parseInt(((JSONObject) jsonIterator).get(Constants.ID)
                                        .toString()),
                                actionType,
                                (String) ((JSONObject) jsonIterator).get(Constants.TYPE),
                                (String) ((JSONObject) jsonIterator).get(Constants.USER),
                                (String) ((JSONObject) jsonIterator).get(Constants.TITLE),
                                grade,
                                season
                        ));
                        case Constants.QUERY -> actions.add(new ActionInputData(
                                Integer.parseInt(((JSONObject) jsonIterator).get(Constants.ID)
                                        .toString()),
                                actionType,
                                (String) ((JSONObject) jsonIterator).get(Constants.OBJECT),
                                genre,
                                (String) ((JSONObject) jsonIterator).get(Constants.SORT),
                                (String) ((JSONObject) jsonIterator).get(Constants.CRITERIA),
                                year,
                                number,
                                Utils.convertJSONArray(words),
                                Utils.convertJSONArray(awards)
                        ));
                        case Constants.RECOMMENDATION -> actions.add(new ActionInputData(
                                Integer.parseInt(((JSONObject) jsonIterator).get(Constants.ID)
                                        .toString()),
                                actionType,
                                (String) ((JSONObject) jsonIterator).get(Constants.TYPE),
                                (String) ((JSONObject) jsonIterator).get(Constants.USERNAME),
                                (String) ((JSONObject) jsonIterator).get(Constants.GENRE)
                        ));
                        default -> {
                        }
                    }
                }
            } else {
                System.out.println("NU EXISTA COMENZI");
                actions = null;
            }

        return actions;
    }
}
