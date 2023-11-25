package org.matsim.contrib.carsharing.qsim;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.matsim.contrib.carsharing.manager.CarsharingManagerInterface;
import org.matsim.contrib.carsharing.manager.supply.CarsharingSupplyInterface;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.components.QSimComponentsConfig;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.utils.timing.TimeInterpretation;

public class CarSharingQSimModule extends AbstractQSimModule {
  public static final String COMPONENT_NAME = "Carsharing";

  @Override
  protected void configureQSim() {
    // addQSimComponentBinding(COMPONENT_NAME).to(ParkCSVehicles.class);
    addQSimComponentBinding(COMPONENT_NAME).to(ParkCSVehicles.class);
  }

  @Provides
  @Singleton
  ParkCSVehicles provideParkCSVehicles(QSim qsim, CarsharingSupplyInterface carsharingSupply) {
    return new ParkCSVehicles(qsim, carsharingSupply);
  }

  @Provides
  @Singleton
  AgentFactory provideAgentFactory(
      Netsim netsim,
      CarsharingManagerInterface carsharingManager,
      TimeInterpretation timeInterpretation) {
    return new CSAgentFactory(netsim, carsharingManager, timeInterpretation);
  }

  public static void configureComponents(QSimComponentsConfig components) {
    components.addNamedComponent(COMPONENT_NAME);
  }
}
