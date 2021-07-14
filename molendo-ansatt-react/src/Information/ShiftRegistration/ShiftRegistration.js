import React, { Component } from 'react'
import { Container, Row, Col } from 'react-bootstrap'
import WeekBox from './WeekBox'
import DayBox from './DayBox'
import NewShift from './NewShift'
import Copying from './Copying'

export default class ShiftRegistration extends Component {
    render() {
        return (
            <Container> 
                <Row>
                    <h1 id="shiftRegistration">Vaktregistrering</h1>
                </Row>
                <Row><h5 className="italic">For tiden kun tilgjengelig på norsk</h5></Row>
                
                <WeekBox />
                <br /> <br /> <br />
                <DayBox />

                <br /> <br /> <br />
                <NewShift />

                <br /> <br /> <br />
                <Copying />
            </Container>
        )
    }
}
