// Karate evalua este fichero como UNA sola expresion. Con varias funciones sueltas a
// nivel superior el motor aborta con "SyntaxError: Expected ) but found function" y
// NINGUN escenario llega a ejecutarse -comprobado en authorization-service:
// "scenarios: 9 | passed: 0 | failed: 9"-. Envolverlo en una expresion que devuelve fn
// deja las funciones auxiliares donde estaban y arregla la sintaxis.
(function () {
function fn() {
  var env = karate.env || 'dev';
  karate.log('karate.env system property was:', env);

  var config = buildBaseConfig(env);
  configureAuth(config);
  configureKarate(config);
  logConfig(config);

  return config;
}

function buildBaseConfig(env) {
  var config = {
    env: env,
    apiPath: '/api/v1',
    healthPath: '/actuator/health',
    debug: false,
    retryEnabled: true,
    retryCount: 3,
    retryInterval: 1000,
    endpoints: {
      health: '/actuator/health',
      healthLive: '/actuator/health/liveness',
      healthReady: '/actuator/health/readiness',
      catalogos: '/api/v1/catalogos'
    }
  };

  if (env === 'dev') {
    applyDevDefaults(config);
  } else {
    applyRemoteEnv(config, env);
  }

  return config;
}

function applyDevDefaults(config) {
  config.baseUrl = karate.properties['base.url'] || 'http://localhost:8080';
  config.debug = true;
  config.timeout = 30000;
  config.oauth2TokenUrl = karate.properties['oauth2.token.url']
      || 'http://localhost:8180/auth/realms/spring-boot/protocol/openid-connect/token';
  config.oauth2ClientId = karate.properties['oauth2.client.id'] || 'backend-service';
  config.oauth2ClientSecret = karate.properties['oauth2.client.secret'] || 'secret';
  config.oauth2Username = karate.properties['oauth2.username'] || '';
  config.oauth2Password = karate.properties['oauth2.password'] || '';
  config.stressThreads = Number.parseInt(karate.properties['stress.threads'] || '5', 10);
}

function applyRemoteEnv(config, env) {
  config.baseUrl = requireProperty('base.url', env);
  config.oauth2TokenUrl = requireProperty('oauth2.token.url', env);
  config.oauth2ClientId = requireProperty('oauth2.client.id', env);
  config.oauth2ClientSecret = karate.properties['oauth2.client.secret'] || '';
  config.oauth2Username = karate.properties['oauth2.username'] || '';
  config.oauth2Password = karate.properties['oauth2.password'] || '';
  config.stressThreads = Number.parseInt(karate.properties['stress.threads'] || '10', 10);

  const isHigherEnv = (env === 'preprod' || env === 'prod');
  config.timeout = Number.parseInt(karate.properties['timeout'] || (isHigherEnv ? '60000' : '30000'), 10);
  config.retryCount = isHigherEnv ? 5 : 3;

  if (env === 'prod') {
    karate.log('PRODUCCION: Solo se permiten smoke tests, NO stress tests.');
  }
}

function requireProperty(name, env) {
  const value = karate.properties[name];
  if (!value) {
    karate.fail('ERROR: -D' + name + ' es obligatoria para el ambiente "' + env + '". '
        + 'En CI se inyecta desde el ConfigMap/Secret del namespace.');
  }
  return value;
}

function configureAuth(config) {
  config.authToken = karate.properties['jwt.token'] || karate.properties['auth.token'] || '';

  // Basta con usuario -flujo de contrasena- o con secreto -cliente confidencial-.
  // Exigir el secreto dejaba fuera el unico flujo que el Keycloak del marco admite.
  if (!config.authToken && config.oauth2ClientId && (config.oauth2Username || config.oauth2ClientSecret)) {
    config.authToken = fetchOAuth2Token(config) || '';
  }

  config.headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'User-Agent': 'Karate-Test-Client/1.5.0'
  };

  if (config.authToken) {
    config.headers['Authorization'] = 'Bearer ' + config.authToken;
  }

  config.getNewToken = function() {
    return fetchOAuth2Token(config);
  };

  config.waitWithBackoff = function(attempt) {
    const waitTime = Math.min(1000 * Math.pow(2, attempt), 10000);
    java.lang.Thread.sleep(waitTime);
  };
}

function fetchOAuth2Token(config) {
  karate.log('Obteniendo token JWT desde OAuth2...');
  try {
    const tokenResponse = karate.callSingle('classpath:integration/oauth2-token-helper.feature', {
      tokenUrl: config.oauth2TokenUrl,
      clientId: config.oauth2ClientId,
      clientSecret: config.oauth2ClientSecret,
      username: config.oauth2Username,
      password: config.oauth2Password
    });
    if (tokenResponse?.access_token) {
      karate.log('Token JWT obtenido exitosamente');
      return tokenResponse.access_token;
    }
    karate.log('WARNING: No se pudo obtener token JWT');
  } catch (e) {
    karate.log('WARNING: Error al obtener token JWT:', e.message);
  }
  return null;
}

function configureKarate(config) {
  karate.configure('connectTimeout', config.timeout);
  karate.configure('readTimeout', config.timeout);
  karate.configure('ssl', true);

  if (config.debug) {
    karate.configure('logPrettyRequest', true);
    karate.configure('logPrettyResponse', true);
  }

  if (config.retryEnabled) {
    karate.configure('retry', { count: config.retryCount, interval: config.retryInterval });
  }
}

function logConfig(config) {
  karate.log('===================================');
  karate.log('Karate Configuration');
  karate.log('===================================');
  karate.log('Environment:', config.env);
  karate.log('Base URL:', config.baseUrl);
  karate.log('API Path:', config.apiPath);
  karate.log('Timeout:', config.timeout, 'ms');
  karate.log('Debug:', config.debug);
  karate.log('Stress Threads:', config.stressThreads);
  karate.log('Auth Token:', config.authToken ? '[CONFIGURED]' : '[NOT SET]');
  karate.log('OAuth2 URL:', config.oauth2TokenUrl || '[NOT SET]');
  karate.log('===================================');
}
  return fn;
})()
