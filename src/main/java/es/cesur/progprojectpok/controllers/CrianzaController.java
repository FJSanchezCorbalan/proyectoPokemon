package es.cesur.progprojectpok.controllers;

import es.cesur.progprojectpok.clases.Pokemon;
import es.cesur.progprojectpok.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;


public class CrianzaController {

    @FXML
    private Button irMenuFromCrianzaButton;


    @FXML
    private ListView<Pokemon> machoViewList;

    @FXML
    private ListView<Pokemon> hembraViewList;

    @FXML
    private List<Pokemon> pokemonMacho;

    @FXML
    private List<Pokemon> pokemonHembra;

    @FXML

    private TextField logCrianza;

    @FXML
    private TextField logOnAction;

    private Pokemon machoSeleccionado;

    private Pokemon hembraSeleccionada;

    private Pokemon pokemonHijo;


    public void initialize() {

        cargarMacho();
        cargarHembra();

        machoViewList.setItems(FXCollections.observableArrayList(pokemonMacho));
        hembraViewList.setItems(FXCollections.observableArrayList(pokemonHembra));
    }


    private List<Pokemon> cargarPokemonesDesdeBD(char sexo) {
        List<Pokemon> pokemones = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM POKEMON WHERE SEXO = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(sexo));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String nombre = resultSet.getString("MOTE");
                int numPokedex = resultSet.getInt("NUM_POKEDEX");
                int ataque = resultSet.getInt("ATAQUE");
                int defensa = resultSet.getInt("DEFENSA");
                int ataqueEspecial = resultSet.getInt("AT_ESPECIAL");
                int defensaEspecial = resultSet.getInt("DEF_ESPECIAL");
                int velocidad = resultSet.getInt("VELOCIDAD");
                int nivel = resultSet.getInt("NIVEL");
                int experiencia = resultSet.getInt("EXPERIENCIA");
                int vitalidad = resultSet.getInt("VITALIDAD");
                int idPokemon = resultSet.getInt("ID_POKEMON");
                Pokemon pokemon = new Pokemon(nombre, numPokedex, ataque, defensa, ataqueEspecial, defensaEspecial, velocidad, nivel, experiencia, vitalidad, idPokemon);
                pokemones.add(pokemon);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar los Pokémon desde la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        return pokemones;
    }

    private void cargarMacho() {
        pokemonMacho = cargarPokemonesDesdeBD('M');
    }

    private void cargarHembra() {
        pokemonHembra = cargarPokemonesDesdeBD('H');
    }


    @FXML
    private void irMenuFromCrianzaOnAction() {

        Stage stage = (Stage) irMenuFromCrianzaButton.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/cesur/progprojectpok/view/menu-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 590, 600);
            Stage menuStage = new Stage();
            menuStage.setTitle("Menu");
            menuStage.setScene(scene);
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Maneja el error apropiadamente
        }
    }

    @FXML
    private void realizarCrianzaOnAction() {


        machoSeleccionado = machoViewList.getSelectionModel().getSelectedItem();
        hembraSeleccionada = hembraViewList.getSelectionModel().getSelectedItem();

        if (machoSeleccionado == null || hembraSeleccionada == null) {
            logCrianza.setText("Debe seleccionar un macho y una hembra para la crianza.");
            return;
        }

        if (machoSeleccionado.getNumPokedex() != hembraSeleccionada.getNumPokedex()) {
            logCrianza.setText("Los Pokémon seleccionados deben ser de la misma especie.");
            return;
        }

        if (machoSeleccionado.getSexo() != hembraSeleccionada.getSexo()) {
            logCrianza.setText("Los Pokémon seleccionados deben tener sexos diferentes.");
            return;
        }






        if (machoSeleccionado.getFertilidad() > 0 && hembraSeleccionada.getFertilidad() > 0){

            pokemonHijo = generarHijo(machoSeleccionado, hembraSeleccionada);

        } else {
            logCrianza.appendText("El pokemon no tiene suficiente fertilidad");
        }



        if (pokemonHijo != null) {
            // Reducir la fertilidad de los padres en 1 punto
            reducirFertilidadPadres(machoSeleccionado);
            reducirFertilidadPadres(hembraSeleccionada);

            // Mostrar mensaje de éxito y agregar el hijo a la base de datos
            logCrianza.setText("¡Crianza realizada con éxito! Abra el huevo");

        } else {
            logCrianza.setText("Error al generar el Pokémon hijo.");
        }


    }

    private Pokemon generarHijo(Pokemon machoSeleccionado, Pokemon hembraSeleccionada) {
        // Crear un nuevo Pokémon hijo con las características de los padres
        pokemonHijo= new Pokemon();
        pokemonHijo.setNombre(machoSeleccionado.getNombre()); // Tomar el nombre de cualquiera de los padres
        pokemonHijo.setNumPokedex(machoSeleccionado.getNumPokedex()); // Mismo número de Pokédex que los padres
        pokemonHijo.setSexo(randomSex()); // Sexo aleatorio para el hijo
        pokemonHijo.setTipo1(machoSeleccionado.getTipo1()); // Mismo tipo que los padres
        pokemonHijo.setTipo2(machoSeleccionado.getTipo2()); // Mismo tipo que los padres
        pokemonHijo.setAtaque(Math.max(machoSeleccionado.getAtaque(), hembraSeleccionada.getAtaque())); // Mejor ataque de los padres
        pokemonHijo.setDefensa(Math.max(machoSeleccionado.getDefensa(), hembraSeleccionada.getDefensa())); // Mejor defensa de los padres
        pokemonHijo.setAtaqueEspecial(Math.max(machoSeleccionado.getAtaqueEspecial(), hembraSeleccionada.getAtaqueEspecial())); // Mejor ataque especial de los padres
        pokemonHijo.setDefensaEspecial(Math.max(machoSeleccionado.getDefensaEspecial(), hembraSeleccionada.getDefensaEspecial())); // Mejor defensa especial de los padres
        pokemonHijo.setVelocidad(Math.max(machoSeleccionado.getVelocidad(), hembraSeleccionada.getVelocidad())); // Mejor velocidad de los padres
        pokemonHijo.setNivel(1); // Nivel inicial del hijo
        pokemonHijo.setExperiencia(0); // Experiencia inicial del hijo
        pokemonHijo.setVitalidad(Math.max(machoSeleccionado.getVitalidad(),hembraSeleccionada.getVitalidad())); // Vitalidad promedio de los padres
        return pokemonHijo;
    }



    private void reducirFertilidadPadres(Pokemon pokemon) {
        // Reducir la fertilidad del Pokémon en 1 punto
        int nuevaFertilidad = pokemon.getFertilidad() - 1;
        pokemon.setFertilidad(Math.max(nuevaFertilidad, 0)); // La fertilidad no puede ser negativa
    }

    private char randomSex() {
        return Math.random() < 0.5 ? 'M' : 'H'; // Generar un sexo aleatorio ('M' o 'H')
    }




    private void insertarPokemonEnBD(Pokemon pokemon) throws SQLException {
        // Conectar a la base de datos
        try (Connection connection = DBConnection.getConnection()) {
            // Preparar la consulta SQL
            String sql = "INSERT INTO POKEMON (NUM_POKEDEX, MOTE, CAJA, ATAQUE, AT_ESPECIAL, DEFENSA, DEF_ESPECIAL, VELOCIDAD, NIVEL, FERTILIDAD, SEXO, ESTADO, EXPERIENCIA, VITALIDAD) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            // Establecer los valores de los parámetros
            statement.setInt(1, pokemon.getNumPokedex());
            statement.setString(2, pokemon.getNombre());
            statement.setInt(3, 1); // Ajustar el valor de la caja según corresponda
            statement.setInt(4, pokemon.getAtaque());
            statement.setInt(5, pokemon.getAtaqueEspecial());
            statement.setInt(6, pokemon.getDefensa());
            statement.setInt(7, pokemon.getDefensaEspecial());
            statement.setInt(8, pokemon.getVelocidad());
            statement.setInt(9, pokemon.getNivel());
            statement.setInt(10, pokemon.getFertilidad());
            statement.setString(11, String.valueOf(pokemon.getSexo()));
            statement.setString(12, "Normal"); // Establecer el estado según corresponda
            statement.setInt(13, pokemon.getExperiencia());
            statement.setInt(14, pokemon.getVitalidad());
            // Ejecutar la consulta
            statement.executeUpdate();
        }
    }

    @FXML
    public void abrirHuevoOnAction() {
        if (pokemonHijo == null) {
            logCrianza.setText("No hay huevo que abrir. Realiza primero la crianza.");
            return;
        }

        try {
            // Insertar el Pokémon hijo en la base de datos
            insertarPokemonEnBD(pokemonHijo);

            // Mostrar un mensaje indicando que se agregó el Pokémon a la base de datos
            logCrianza.setText("¡Huevo abierto! Nuevo Pokémon agregado a la base de datos " + pokemonHijo.getNombre());
        } catch (SQLException e) {
            logCrianza.setText("Error al agregar el nuevo Pokémon a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }








}


