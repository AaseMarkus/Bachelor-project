import React, { Component } from 'react'
import DayLock from './Buttons/DayLock'
import DayReset from './Buttons/DayReset'
import DayCopy from './Buttons/DayCopy'
import IndividualShift from './Buttons/IndividualShift'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class DayButtons extends Component {
    render() {
        return (
            <div>
                <Row>
                    <h3>Dagsknapper</h3>
                </Row>
                <Row>
                    Knappene som brukes for å manipulere dagsboksene er ganske like knappen som brukes for ukesboksene,
                    men er kun for en dag av gangen, i stedet for hele uken.
                </Row>

                <DayLock />
                <br />
                <DayReset />
                <br />
                <DayCopy />
                <br /> <br />
                <IndividualShift />
            </div>
        )
    }
}
