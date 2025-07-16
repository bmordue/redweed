{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "micronaut-dev-shell";
  
  buildInputs = with pkgs; [
    # Java Development Kit
    jdk21
    
    # Build tools
    gradle
    
    # Development tools
    git
    curl
    jq
    
    # Container tools
#    docker
#    docker-compose
    
    # Testing tools
#    testcontainers
    
    # HTTP client for API testing
    httpie
    
    # Process management
    tmux
    
  ];
  
  shellHook = ''
    echo "🚀 Micronaut Development Environment"
    echo "Java version: $(java -version 2>&1 | head -1)"
    echo "Gradle version: $(gradle --version | grep Gradle)"
    echo "Micronaut CLI version: $(mn --version)"
    echo ""
    echo "Quick start commands:"
    echo "  mn create-app com.example.myapp --build=gradle --lang=java"
    echo ""
    echo "Environment variables:"
    export JAVA_HOME="${pkgs.jdk21}"
    export PATH="$JAVA_HOME/bin:$PATH"
    
    
    # Maven/Gradle cache directories
    export GRADLE_USER_HOME="$HOME/.gradle"
    
    # Docker environment
#    export DOCKER_BUILDKIT=1
#    export COMPOSE_DOCKER_CLI_BUILD=1
    
    # Create local postgres directory if it doesn't exist
 #   if [ ! -d "$PGDATA" ]; then
 #     echo "Initializing PostgreSQL database..."
 #     initdb -D "$PGDATA" --auth-local=trust --auth-host=trust
 #   fi
    
    # Function to start/stop local postgres
 #   pg_start() {
 #     pg_ctl -D "$PGDATA" -l "$PGDATA/postgresql.log" start
 #     echo "PostgreSQL started on port $PGPORT"
 #   }
    
 #   pg_stop() {
 #     pg_ctl -D "$PGDATA" stop
 #     echo "PostgreSQL stopped"
 #   }
    
 #   pg_status() {
 #     pg_ctl -D "$PGDATA" status
 #   }
    
    # Function to create a new Micronaut project
    mn_new() {
      if [ -z "$1" ]; then
        echo "Usage: mn_new <app-name> [build-tool]"
        echo "Example: mn_new myapp gradle"
        return 1
      fi
      
      local app_name="$1"
      local build_tool="''${2:-gradle}"
      
      mn create-app "com.example.$app_name" \
        --build="$build_tool" \
        --lang=java \
        --test=junit \
        --features=data-jdbc,flyway,postgres,validation,security-jwt,openapi
      
      echo "Created Micronaut app: $app_name"
      echo "cd $app_name && ./$build_tool run"
    }
    
    # Function to run tests with testcontainers
    test_with_containers() {
      export TESTCONTAINERS_RYUK_DISABLED=true
      if command -v gradle &> /dev/null; then
        ./gradlew test
      elif command -v mvn &> /dev/null; then
        ./mvnw test
      else
        echo "No build tool found (gradle/maven)"
      fi
    }
    
    echo "Available functions:"
    echo "  pg_start    - Start PostgreSQL"
    echo "  pg_stop     - Stop PostgreSQL"
    echo "  pg_status   - Check PostgreSQL status"
    echo "  mn_new      - Create new Micronaut project"
    echo "  test_with_containers - Run tests with TestContainers"
    echo ""
  '';
  
  # Environment variables
  JAVA_OPTS = "-Xmx2g -Xms512m";
  MICRONAUT_ENVIRONMENTS = "dev";
  
  # Prevent Gradle daemon issues in Nix
  GRADLE_OPTS = "-Dorg.gradle.daemon=false";
}
