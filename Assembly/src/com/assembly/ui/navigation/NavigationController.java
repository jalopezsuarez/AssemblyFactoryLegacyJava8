/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.ui.navigation;

import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.assembly.ui.themes.DimensionStyle;
import java.awt.Window;

/**
 *
 * @author Administrator
 */
public final class NavigationController
{

    private static JFrame frames = null;
    private final Container container = new Container();

    private final HashMap<ControllerInterface, ControllerState> controllers = new HashMap();
    private final HashMap<Component, Window> windows = new HashMap();

    private enum ControllerState
    {
        None, WillAppear, DidAppear, WillDisappear, DidDisappear;
    }

// =======================================================
    private static final NavigationController INSTANCE = new NavigationController();

    public static NavigationController instance()
    {
        return INSTANCE;
    }

    // =======================================================  
    public void rootController(JFrame frame, JPanel controller, DimensionStyle dimension)
    {
        frames = frame;

        // =======================================================
        container.setLayout(new CardLayout());

        // =======================================================
        if (controller != null && controller instanceof ControllerInterface)
        {
            controller.addAncestorListener(new AncestorListener()
            {
                @Override
                public void ancestorAdded(AncestorEvent event)
                {
                    Component source = event.getComponent();
                    if (source != null && source instanceof JPanel)
                    {
                        if (source.getName() != null && source.getName().length() > 0)
                        {
                            frames.setTitle(source.getName());
                        }
                    }
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        if (controllers.containsKey(component) && controllers.get(component).equals(ControllerState.WillAppear))
                        {
                            controllers.remove(component);
                            component.viewDidAppear();
                        }
                    }
                }

                @Override
                public void ancestorRemoved(AncestorEvent event)
                {
                    Component source = event.getComponent();
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        if (controllers.containsKey(component) && controllers.get(component).equals(ControllerState.WillDisappear))
                        {
                            controllers.remove(component);
                            component.viewDidDisappear();
                        }
                    }
                }

                @Override
                public void ancestorMoved(AncestorEvent event
                )
                {
                }
            });
        }

        // =======================================================
        Component source = controller;
        if (source != null && source instanceof ControllerInterface)
        {
            ControllerInterface component = (ControllerInterface) source;
            controllers.put(component, ControllerState.WillAppear);
            component.viewWillAppear();
        }

        // =======================================================
        container.add(controller);
        ((CardLayout) container.getLayout()).last(container);

        // =======================================================
        frame.setLayout(new BorderLayout());
        frame.add(container, BorderLayout.CENTER);

        frame.setLocation(dimension.getX(), dimension.getY());
        frame.setSize(new Dimension(dimension.getWidth(), dimension.getHeight()));
        frame.setPreferredSize(new Dimension(dimension.getWidth(), dimension.getHeight()));
        frame.pack();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setState(Frame.NORMAL);
        frame.setExtendedState(Frame.NORMAL);

        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();

        if (dimension.isFullScreen())
        {
            GraphicsDevice screenDevice = MouseInfo.getPointerInfo().getDevice();
            screenDevice.setFullScreenWindow(frame);
        }
    }

    public void pushController(JPanel controller)
    {
        // =======================================================
        if (controller != null && controller instanceof ControllerInterface)
        {
            controller.addAncestorListener(new AncestorListener()
            {
                @Override
                public void ancestorAdded(AncestorEvent event)
                {
                    Component source = event.getComponent();
                    if (source != null && source instanceof JPanel)
                    {
                        if (source.getName() != null && source.getName().length() > 0)
                        {
                            frames.setTitle(source.getName());
                        }
                    }
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        if (controllers.containsKey(component) && controllers.get(component).equals(ControllerState.WillAppear))
                        {
                            controllers.remove(component);
                            component.viewDidAppear();
                        }
                    }
                }

                @Override
                public void ancestorRemoved(AncestorEvent event)
                {
                    Component source = event.getComponent();
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        if (controllers.containsKey(component) && controllers.get(component).equals(ControllerState.WillDisappear))
                        {
                            controllers.remove(component);
                            component.viewDidDisappear();
                        }
                    }
                }

                @Override
                public void ancestorMoved(AncestorEvent event
                )
                {
                }
            });
        }

        // =======================================================        
        if (container.getComponentCount() > 0)
        {
            Component source = container.getComponent(container.getComponentCount() - 1);
            if (source != null && source instanceof ControllerInterface)
            {
                ControllerInterface component = (ControllerInterface) source;
                controllers.put(component, ControllerState.WillDisappear);
                component.viewWillDisappear();
            }
        }

        // =======================================================
        Component source = controller;
        if (source != null && source instanceof ControllerInterface)
        {
            ControllerInterface component = (ControllerInterface) source;
            controllers.put(component, ControllerState.WillAppear);
            component.viewWillAppear();
        }

        // =======================================================
        container.add(controller);
        ((CardLayout) container.getLayout()).last(container);
    }

    public boolean dismissController()
    {
        boolean dismissController = false;

        // =======================================================        
        if (container.getComponentCount() > 1)
        {
            Component source = container.getComponent(container.getComponentCount() - 1);
            if (source != null && source instanceof ControllerInterface)
            {
                ControllerInterface component = (ControllerInterface) source;
                controllers.put(component, ControllerState.WillDisappear);
                component.viewWillDisappear();
            }
        }

        // =======================================================
        if (container.getComponentCount() > 1)
        {
            Component source = container.getComponent(container.getComponentCount() - 2);
            if (source != null && source instanceof ControllerInterface)
            {
                ControllerInterface component = (ControllerInterface) source;
                controllers.put(component, ControllerState.WillAppear);
                component.viewWillAppear();
            }
        }

        // =======================================================
        if (container.getComponentCount() > 1)
        {
            dismissController = true;
            container.remove(container.getComponentCount() - 1);
            ((CardLayout) container.getLayout()).last(container);
        }

        return dismissController;
    }

    public void pushWindow(JPanel controller, Windowed... windowed)
    {
        boolean windowedSingleton = false;
        boolean windowedModal = false;

        Window window = null;
        for (Windowed attribute : windowed)
        {
            if (attribute.equals(Windowed.Singleton))
            {
                windowedSingleton = true;
            }
            else if (attribute.equals(Windowed.Modal))
            {
                windowedModal = true;
            }
        }

        if (windowedSingleton)
        {
            for (HashMap.Entry<Component, Window> entry : windows.entrySet())
            {
                Component singleton = entry.getKey();
                if (controller.getClass().isInstance(singleton))
                {
                    window = entry.getValue();
                }
            }
        }

        if (window == null && windowedModal)
        {
            window = new DialogController(controller, windowed);
            windows.put(controller, window);
        }
        else if (window == null)
        {
            window = new FrameController(controller, windowed);
            windows.put(controller, window);
        }

        // =======================================================
        window.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowOpened(WindowEvent event)
            {
                super.windowOpened(event);

                Component window = event.getComponent();
                if (window != null && window instanceof WindowController)
                {
                    WindowController instance = (WindowController) window;
                    Component source = instance.getWindowPane();
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        if (controllers.containsKey(component) && controllers.get(component).equals(ControllerState.WillAppear))
                        {
                            controllers.remove(component);
                            component.viewDidAppear();
                        }
                    }
                }
            }

            @Override
            public void windowClosing(WindowEvent event)
            {
                super.windowClosing(event);

                Component window = event.getComponent();
                if (window != null && window instanceof WindowController)
                {
                    WindowController instance = (WindowController) window;
                    Component source = instance.getWindowPane();
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        controllers.put(component, ControllerState.WillDisappear);
                        component.viewWillDisappear();
                    }
                }
            }

            @Override
            public void windowClosed(WindowEvent event)
            {
                super.windowClosed(event);

                Component window = event.getComponent();
                if (window != null && window instanceof WindowController)
                {
                    WindowController instance = (WindowController) window;
                    Component source = instance.getWindowPane();
                    if (source != null && source instanceof ControllerInterface)
                    {
                        ControllerInterface component = (ControllerInterface) source;
                        if (controllers.containsKey(component) && controllers.get(component).equals(ControllerState.WillDisappear))
                        {
                            windows.remove(window);
                            controllers.remove(component);
                            component.viewDidDisappear();
                        }
                    }
                }
            }
        });

        // =======================================================
        Component source = (Component) controller;
        if (source != null && source instanceof ControllerInterface)
        {
            ControllerInterface component = (ControllerInterface) source;
            controllers.put(component, ControllerState.WillAppear);
            component.viewWillAppear();
        }

        // =======================================================
        if (window instanceof WindowController)
        {
            WindowController instance = (WindowController) window;
            instance.showWindow();
        }

    }

    public void dismissWindow(JPanel controller)
    {
        if (controller != null && controller instanceof ControllerInterface)
        {
            ControllerInterface component = (ControllerInterface) controller;
            controllers.put(component, ControllerState.WillDisappear);
            component.viewWillDisappear();
        }

        if (controller != null && windows.containsKey(controller) && windows.get(controller) instanceof WindowController)
        {
            Window window = windows.get(controller);
            windows.remove(controller);
            window.dispose();
        }

    }
}
