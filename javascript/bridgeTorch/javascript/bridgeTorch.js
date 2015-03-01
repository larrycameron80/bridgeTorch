(function() {
    function Person(id, timeToCrossMinutes) {
        this.id = id;
        this.timeToCrossMinutes = timeToCrossMinutes;
    }


    var TorchSide = {
        LEFT: 'LEFT',
        RIGHT: 'RIGHT',
        flip: function(torchSide) {
            if(this.LEFT === torchSide) {
                return this.RIGHT;
            } else if(this.RIGHT === torchSide) {
                return this.LEFT;
            } else {
                throw 'Unknown torchSide ' + torchSide;
            }
        }
    };


    function PersonsToCross(personsToCross) {
        this.personsToCross = personsToCross;
    }

    PersonsToCross.prototype.containsPerson = function(targetPerson) {
        for(var i = 0; i < this.personsToCross.length; i++) {
            var person = this.personsToCross[i];
            var personId = person.id;
            var targetPersonId = targetPerson.id;
            var matches = personId === targetPersonId;
            if(matches) {
                return true;
            }
        }

        return false;
    };

    PersonsToCross.prototype.timeToCrossMinutes = function () {
        var timesToCrossMinutes = this.personsToCross.map(function(person) { return person.timeToCrossMinutes });
        return timesToCrossMinutes.reduce(function (max, current) {
            if (current > max) {
                return current;
            } else {
                return max;
            }
        });
    };


    function State(leftSide, rightSide, torchSide, elapsedTimeMinutes) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.torchSide = torchSide;
        this.elapsedTimeMinutes = elapsedTimeMinutes;
    }

    State.prototype.nextPossibleStates = function () {
        if(TorchSide.LEFT === this.torchSide) {
            return this.nextPossibleStatesFromLeft();
        } else if(TorchSide.RIGHT === this.torchSide) {
            return this.nextPossibleStatesFromRight();
        } else {
            throw 'Unknown torchSide ' + this.torchSide;
        }
    };

    State.prototype.nextPossibleStatesFromLeft = function() {
        var possiblePersonsToCross = this.possiblePersonsToCrossFromLeft();
        return possiblePersonsToCross.map(function (personsToCross) {
            return this.nextStateFromLeft(personsToCross);
        }, this);
    };

    State.prototype.nextPossibleStatesFromRight = function () {
        var possiblePersonsToCross = this.possiblePersonsToCrossFromRight();
        return possiblePersonsToCross.map(function (personsToCross) {
            return this.nextStateFromRight(personsToCross);
        }, this);
    };

    State.prototype.possiblePersonsToCrossFromLeft = function() {
        var possiblePersonsToCross = [];

        for(var i = 0; i < this.leftSide.length; i++) {
            for(var j = i + 1; j < this.leftSide.length; j++) {
                var person1 = this.leftSide[i];
                var person2 = this.leftSide[j];

                var personsToCross = new PersonsToCross([person1, person2]);

                possiblePersonsToCross.push(personsToCross);
            }
        }

        return possiblePersonsToCross;
    };

    State.prototype.possiblePersonsToCrossFromRight = function() {
        var possiblePersonsToCross = [];

        for(var i = 0; i < this.rightSide.length; i++) {
            var person = this.rightSide[i];
            var personsToCross = new PersonsToCross([person]);

            possiblePersonsToCross.push(personsToCross);
        }

        return possiblePersonsToCross;
    };

    State.prototype.nextStateFromLeft = function(personsToCross) {
        var nextLeftSide = this.leftSide.filter(function(person) {
            return !personsToCross.containsPerson(person);
        });

        var nextRightSide = this.rightSide.concat(personsToCross.personsToCross);

        var nextTorchSide = TorchSide.flip(this.torchSide);

        var nextElapsedTimeMinutes = this.elapsedTimeMinutes + personsToCross.timeToCrossMinutes();

        return new State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes);
    };

    State.prototype.nextStateFromRight = function(personsToCross) {
        var nextLeftSide = this.leftSide.concat(personsToCross.personsToCross);

        var nextRightSide = this.rightSide.filter(function(person) {
            return !personsToCross.containsPerson(person);
        }, this);

        var nextTorchSide = TorchSide.flip(this.torchSide);

        var nextElapsedTimeMinutes = this.elapsedTimeMinutes + personsToCross.timeToCrossMinutes();

        return new State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes);
    };

    State.prototype.isDone = function() {
        return this.leftSide.length === 0;
    };


    function States(states) {
        this.states = states;
    }

    States.prototype.nextStates = function() {
        var nextStatesArray = [];

        this.states.forEach(function(state) {
            var nextPossibleStates = state.nextPossibleStates();
            nextStatesArray = nextStatesArray.concat(nextPossibleStates);
        });

        return new States(nextStatesArray);
    };

    States.prototype.finalStates = function() {
        var currentStates = this;

        while(true) {
            var allDone = currentStates.isAllDone();
            if(allDone) {
                return currentStates;
            }

            currentStates = currentStates.nextStates();
        }
    };

    States.prototype.finalELapsedTimeMinutes = function() {
        var finalStates = this.finalStates();

        return finalStates.states.map(function (state) {
            return state.elapsedTimeMinutes;
        });
    };

    States.prototype.minFinalElapsedTimeMinutes = function() {
        var finalElapsedTimeMinutes = this.finalELapsedTimeMinutes();

        return finalElapsedTimeMinutes.reduce(function (min, current) {
            if (min > current) {
                return current;
            } else {
                return min;
            }
        });
    };

    States.prototype.isAllDone = function() {
        return this.states.every(function (state) {
            return state.isDone();
        });
    };


    var initialState = new State([new Person('A', 1), new Person('B', 2), new Person('C', 5), new Person('D', 8)], [], TorchSide.LEFT, 0);
    var initialStates = new States([initialState]);
    var minFinalElapsedTimeMinutes = initialStates.minFinalElapsedTimeMinutes();
    console.log(minFinalElapsedTimeMinutes)

})();