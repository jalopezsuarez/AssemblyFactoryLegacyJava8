package com.assembly.ui.themes;

import java.util.HashMap;
import javax.swing.JComponent;

public final class ThemeFactory
{

    public static final String ReferenceThemes = "/res/themes/";
    public static final String ReferenceFile = "theme.css";

    private static final ThemeFactory INSTANCE = new ThemeFactory();

    public static ThemeFactory instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private ThemeFactory()
    {
        // CARGA TEMAS theme.css rellena el array themes
        // Usage por defecto carga estilos "res/themes/theme.css"
        // Estilos componentes: ThemeFactory.css("miboton", labelEstadoDispositivo);
        // Estilos globales: ThemeFactory.theme(this);
    }

    /**
     * Carga los temas localizados en res.themes/theme.css de forma
     * esttica. <br>
     * Cuando se instancia un componente utiliza los atributos de estilos
     * definidos: <br>
     * <br>
     * themes.TextField.foreColor = #ffffff<br>
     * themes.TextField.backColor = #000000<br>
     */
    private final static HashMap<Class<? extends JComponent>, ThemeStyle> themes = new HashMap();

    public static ThemeStyle theme(Class<? extends JComponent> clazz)
    {
        ThemeStyle theme = null;
        if (themes.containsKey(clazz))
        {
            theme = themes.get(clazz);
        }
        return theme;
    }

    public static void theme(JComponent component)
    {

        // aplica estilos globales
        // for fdsfsd(component)
        {
            //tipode copmonente = compoent.tipo();
            // if (themes.containsKey(clazz))
            {
                // theme = themes.get(clazz);
            }
            // component.stylize(theme)
        }
        // aplica estilos especificos de componentes
        {

        }
    }

    public static void css(String style, JComponent component)
    {
        // inserta en un mapa de componentes y estilos
    }
}
