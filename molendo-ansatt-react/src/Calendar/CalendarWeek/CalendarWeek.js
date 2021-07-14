import React, { Component } from 'react'
import { Button, OverlayTrigger, Popover } from 'react-bootstrap'
import i18n from './../../Language/i18n'
import CalendarDay from './../CalendarDay/CalendarDay'
import WeeklyCopyPopout from './../WeeklyCopyPopout/WeeklyCopyPopout'
import CopyIcon from './../../Icons/CopyIcon'
import TrashIcon from './../../Icons/TrashIcon'
import MaximizeIcon from './../../Icons/MaximizeIcon'
import MinimizeIcon from './../../Icons/MinimizeIcon'
import LockedIcon from './../../Icons/LockedIcon'
import UnlockedIcon from './../../Icons/UnlockedIcon'
import DoubleLeftIcon from './../../Icons/DoubleLeftIcon'
import DoubleRightIcon from './../../Icons/DoubleRightIcon'
import SaveIcon from './../../Icons/SaveIcon'
import './CalendarWeek.css'

export default class CalendarWeek extends Component {
    constructor(props) {
        super(props)
        this.scrollRef = React.createRef()
        
        this.state = {
            minimized: false,
            locked: false,
            showCopy: false
        }

        this.changeMinimized = this.changeMinimized.bind(this)
        this.changeLocked = this.changeLocked.bind(this)
        this.handleScrolling = this.handleScrolling.bind(this)
        this.toggleCopyPopup = this.toggleCopyPopup.bind(this)

    }

    changeMinimized() {
        this.setState(prevState => ({
            minimized: !prevState.minimized
        }))
    }
    
    changeLocked() {
        this.setState(prevState => ({
            locked: !prevState.locked
        }))
    }

    handleScrolling(direction) {
        if (direction === 'left') {
            this.scrollRef && this.scrollRef.current ? 
                (this.scrollRef.current.scrollBy({
                    left: -300, top: 0, behavior: 'smooth'})) : null;
        } else {
            this.scrollRef && this.scrollRef.current ?
                (this.scrollRef.current.scrollBy({
                    left: 300, top: 0, behavior: 'smooth'})) : null;
        }
    }

    // https://weeknumber.net/how-to/javascript
    getWeek(date) {
        date.setHours(0,0,0,0)
        date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7)
        let week1 = new Date(date.getFullYear(), 0, 4)

        return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000
        - 3 + (week1.getDay() + 6) % 7) / 7)
    }

    toggleCopyPopup() {
        this.setState({showCopy: !this.state.showCopy})
    }

    render() {
        let dayList = []
        for(let i = 0; i < 7; i++) {
            let currentDate = new Date(this.props.startDate)
            currentDate.setDate(currentDate.getDate() + i)
            let endDate = new Date(this.props.endDate)
            endDate.setDate(endDate.getDate() - (6 - i))
            dayList[i] = <CalendarDay 
                key={i}
                deleteRostersBetweenDates={this.props.deleteRostersBetweenDates}
                minWeek={this.getWeek(new Date(this.props.minDate))}
                serverTime={this.props.serverTime}
                cssTheme={this.props.cssTheme}
                minDate={this.props.minDate}
                pastDate={this.props.serverTime > endDate}
                replaceRostersOnDate={this.props.replaceRostersOnDate}
                weekAmount={this.props.weekAmount} 
                locked={this.state.locked} 
                addRoster={this.props.addRoster} 
                deleteRoster={this.props.deleteRoster} 
                currentDate={currentDate.getTime()} 
                endDate={endDate.getTime()} 
                weekday={i18n.t('weekdays.'+i)} 
                dailyPlans={this.props.weeklyRoster[i]} />
        }

        let dayWrapper = <div ref={this.scrollRef}
                className={`dayWrapper ${this.state.locked ? "locked" : "unlocked"}`}> 
                {dayList}
            </div>
        
        // Minimize / expand button state
        let minimized;
        if (this.state.minimized) {
            minimized = [<MaximizeIcon key="1" className="headerIcon"/>, <span key="2">{i18n.t('week.expand')}</span>]
        } else {
            minimized = [<MinimizeIcon key="1" className="headerIcon"/>, <span key="2">{i18n.t('week.minimize')}</span>]
        }

        // Locked / unlocked button state
        let lock;
        if (this.state.locked) {
            lock = [<LockedIcon key="1" className="headerIcon"/>, <span key="2">{i18n.t('buttons.locked')}</span>]
        } else {
            lock = [<UnlockedIcon key="1" className="headerIcon"/>, <span key="2">{i18n.t('buttons.lock')}</span>]
        }

        const copyPopover = (
            <Popover id="popover-basic" className={this.props.cssTheme}>
                <Popover.Content>
                    <WeeklyCopyPopout 
                        toggleOverlay={this.toggleCopyPopup}
                        minWeek={this.getWeek(new Date(this.props.minDate))}
                        weekAmount={this.props.weekAmount}
                        rostersToCopy={this.props.weeklyRoster}
                        replaceRostersOnWeek={this.props.replaceRostersOnWeek}
                        minDate={this.props.minDate}
                        serverTime={this.props.serverTime}/>
                </Popover.Content>
            </Popover>
        )

        const deleteStartDate = (
            this.props.serverTime > this.props.startDate ? 
            new Date(this.props.serverTime) : new Date(this.props.startDate)
        )

        return (
            <div className="weekContainer">
                <div className="weekHeader">
                    <h2>{i18n.t('week.week')} {this.getWeek(new Date(this.props.startDate))}</h2>
                    <Button
                        role="img"
                        title={i18n.t('buttons.reset')}
                        variant="secondary"
                        disabled={this.state.locked} 
                        onClick={() => this.props.deleteRostersBetweenDates(deleteStartDate, this.props.endDate)}>
                        <TrashIcon className="headerIcon"/><span>{i18n.t('buttons.reset')}</span>
                    </Button>

                    <OverlayTrigger
                        show={this.state.showCopy}
                        onToggle={this.toggleCopyPopup}
                        trigger="click"
                        rootClose
                        placement="bottom"
                        overlay={copyPopover}>
                        <Button
                            role="img"
                            title={i18n.t('buttons.copy')}
                            variant="secondary">
                            <CopyIcon className="headerIcon"/>
                            <span>{i18n.t('buttons.copy')}</span>
                        </Button>
                    </OverlayTrigger>

                    <Button
                        role="img"
                        title={this.state.locked ? i18n.t('buttons.locked') : i18n.t('buttons.lock')}
                        variant="secondary"
                        onClick={this.changeLocked}>{lock}
                    </Button>

                    <Button
                        role="img"
                        title={i18n.t('buttons.save')}
                        variant="secondary"
                        onClick={this.props.toggleSaveModal}>
                        <SaveIcon className="headerIcon"/>
                        <span>{i18n.t('buttons.save')}</span>
                    </Button> 

                    <Button
                        role="img"
                        title={this.state.minimized ? i18n.t('week.expand') : 
                                i18n.t('week.minimize')}
                        variant="secondary"
                        onClick={this.changeMinimized}>{minimized}
                    </Button>

                    <Button
                        role="img"
                        className="navigate"
                        title={i18n.t('week.scroll-left-title')}
                        onClick={() => this.handleScrolling('left')}
                        variant="secondary">
                        <DoubleLeftIcon className="headerIcon"/>
                    </Button>

                    <span className="navigate">{i18n.t('week.navigate')}</span>

                    <Button
                        role="img"
                        className="navigate"
                        title={i18n.t('week.scroll-right-title')}
                        onClick={() => this.handleScrolling('right')}
                        variant="secondary">
                        <DoubleRightIcon className="headerIcon"/>
                    </Button>
                </div>
                {!this.state.minimized && dayWrapper}
            </div>
        )
    }
}
