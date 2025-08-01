{
  description = "A Nix-based development environment for a monorepo project";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in
      {
        devShells.default = pkgs.mkShellNoCC {
        buildInputs = with pkgs; [
          # from root shell.nix
          jdk21_headless
          jena
          gemini-cli
          nodejs_24

          # from jweed/shell.nix
          gradle
          git
          curl
          jq
          httpie
          tmux
        ];

        shellHook = ''
          export PROJECT_ROOT=$(pwd)
          export DATA_DIR=$PROJECT_ROOT/data
          export MEDIA_DIR=$PROJECT_ROOT/media
          export JENA_HOME=${pkgs.jena}
          export PATH=$JENA_HOME/bin:$PATH

          # Create isolated directories
          mkdir -p $DATA_DIR/{tdb2,temp} $MEDIA_DIR

          # Restrict environment to project
          export HOME=$PROJECT_ROOT/.home
          mkdir -p $HOME

          # Jena configuration
          export JENA_DB_PATH=$DATA_DIR/tdb2
          export TDB_CONFIG="$DATA_DIR/tdb.properties"

          # Create TDB config if it doesn't exist
          if [ ! -f "$TDB_CONFIG" ]; then
            cat > "$TDB_CONFIG" << EOF
# TDB2 Configuration
tdb:DatasetTDB2
tdb:location "$JENA_DB_PATH"
tdb:unionDefaultGraph true
EOF
          fi

          echo "🚀 Monorepo Development Environment"
          echo "Java version: $(java -version 2>&1 | head -1)"
          echo "Gradle version: $(gradle --version | grep Gradle)"
          echo "Node version: $(node --version)"

          # Environment variables from jweed/shell.nix
          export JAVA_HOME="${pkgs.jdk21_headless}"
          export PATH="$JAVA_HOME/bin:$PATH"
          export GRADLE_USER_HOME="$HOME/.gradle"

          echo "Environment ready:"
          echo "  Data store: $JENA_DB_PATH"
          echo "  Media dir:  $MEDIA_DIR"
          echo "  Jena tools: Available in PATH"
        '';

        # from jweed/shell.nix
        JAVA_OPTS = "-Xmx2g -Xms512m";
        MICRONAUT_ENVIRONMENTS = "dev";
        GRADLE_OPTS = "-Dorg.gradle.daemon=false";

        backup-script = pkgs.writeShellScriptBin "backup" ''
          BACKUP_DIR="$PROJECT_ROOT/backups/$(date +%Y%m%d-%H%M%S)"
          mkdir -p "$BACKUP_DIR"
          cp -r "$DATA_DIR" "$MEDIA_DIR" "$BACKUP_DIR/"
          echo "Backup created: $BACKUP_DIR"
        '';
      };
    });
}
