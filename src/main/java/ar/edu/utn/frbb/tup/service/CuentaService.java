package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuentaService {
    CuentaDao cuentaDao = new CuentaDao();

    @Autowired
    ClienteService clienteService;

    // Generar casos de test para darDeAltaCuenta
    // 1 - cuenta existente
    // 2 - cuenta no soportada
    // 3 - cliente ya tiene cuenta de ese tipo
    // 4 - cuenta creada exitosamente
    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }
    
        if (!tipoCuentaSoportada(cuenta)) {
            throw new CuentaNoSoportadaException("El tipo de cuenta y moneda no están soportados.");
        }
        // Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
        // if (!tipoCuentaEstaSoportada(cuenta)) {...}

        clienteService.agregarCuenta(cuenta, dniTitular);
        cuentaDao.save(cuenta);
    }

    private boolean tipoCuentaSoportada(Cuenta cuenta) {
        TipoCuenta tipoCuenta = cuenta.getTipoCuenta();
        TipoMoneda tipoMoneda = cuenta.getMoneda();

        return (tipoCuenta == TipoCuenta.CAJA_AHORRO && tipoMoneda == TipoMoneda.PESOS) ||
                (tipoCuenta == TipoCuenta.CUENTA_CORRIENTE && tipoMoneda == TipoMoneda.PESOS) ||
                (tipoCuenta == TipoCuenta.CAJA_AHORRO && tipoMoneda == TipoMoneda.DOLARES);
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }
}
